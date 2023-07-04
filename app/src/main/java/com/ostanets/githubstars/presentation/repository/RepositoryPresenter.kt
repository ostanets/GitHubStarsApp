package com.ostanets.githubstars.presentation.repository

import com.ostanets.githubstars.data.RepoBody
import com.ostanets.githubstars.data.remote.github.GithubApiService
import com.ostanets.githubstars.di.DaggerGithubNetworkComponent
import com.ostanets.githubstars.domain.AppRepo
import com.ostanets.githubstars.domain.Repo
import com.ostanets.githubstars.domain.Stargazer
import com.ostanets.githubstars.domain.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import moxy.InjectViewState
import moxy.MvpPresenter
import retrofit2.HttpException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.inject.Inject

@InjectViewState
class RepositoryPresenter(private val repository: AppRepo) :
    MvpPresenter<RepositoryView>() {
    @Inject
    lateinit var githubApiService: GithubApiService

    init {
        DaggerGithubNetworkComponent.create().inject(this)
    }

    private var user: User? = null
    private var repo: Repo? = null
    private var groupType = GroupType.DAILY
    private var chartPageNumber = CHART_START_PAGE_NUMBER
    private var pageNumber = START_PAGE_NUMBER
    private var loadMoreAvailable = ALLOW_LOAD_MORE

    fun getRepository(repositoryId: Long) {
        CoroutineScope(Dispatchers.Main).launch {
            repo = getCachedRepository(repositoryId)
            user = getCachedUser(repo!!.ownerId!!)
            sendDataToActivity()

            val newRepo = try {
                getNetworkRepository()
            } catch (e: Exception) {
                when (e) {
                    is HttpException -> {
                        handleHttpException(e)
                        null
                    }

                    is UnknownHostException -> {
                        handleNoInternetConnection()
                        null
                    }

                    is SocketTimeoutException -> {
                        handleNoInternetConnection()
                        null
                    }

                    else -> null
                }
            }

            repo = newRepo
            sendDataToActivity()

            cacheRepository(newRepo)
        }
    }

    private fun handleNoInternetConnection() {
        viewState.showError("No Internet connection, cache loaded")
    }

    private fun handleHttpException(e: HttpException) {
        if (e.code() == 404) {
            viewState.commitStargazers(emptyList())
            viewState.showError("No stargazer found")
        } else {
            viewState.showError("Unexpected HTTP Exception")
        }
    }

    private suspend fun getCachedRepository(repositoryId: Long): Repo {
        var cachedRepository = repository.getRepo(repositoryId)
            ?: throw Exception("User $repositoryId not found")
        cachedRepository = repository.initStargazers(cachedRepository)
        return cachedRepository
    }

    private suspend fun getCachedUser(userId: Long): User {
        return repository.getUser(userId) ?: throw Exception("User $userId not found")
    }

    private fun sendDataToActivity() {
        viewState.setOwner(user!!.login)
        viewState.setRepository(repo!!.name)
        viewState.setStarsCount(repo!!.stargazers.size)
        nextChartPage()
        viewState.setFavorite(repo!!.favourite!!)
        viewState.hideProgressBar()
    }

    private suspend fun getNetworkRepository(): Repo {
        val networkRepository = githubApiService.getRepo(user!!.login, repo!!.name)
        networkRepository.stargazers = findStargazers()
        return networkRepository
    }

    private suspend fun findStargazers(): List<Stargazer> {

        return githubApiService.listStargazers(
            user!!.login,
            repo!!.name,
            pageNumber++,
            GithubApiService.MAXIMUM_PER_PAGE_LIMIT
        )
    }

    fun nextChartPage() {
        var nextPosition = chartPageNumber * CHART_BARS_COUNT
        if (nextPosition + CHART_BARS_COUNT >= repo!!.stargazers.size) {
            when (loadMoreAvailable) {
                ALLOW_LOAD_MORE -> {
                    CoroutineScope(Dispatchers.Main).launch {
                        loadMoreStargazers(groupType)
                    }
                    if (nextPosition >= repo!!.stargazers.size) {
                        nextPosition = repo!!.stargazers.size - CHART_BARS_COUNT - 1
                    }
                }

                DISMISS_LOAD_MORE -> {
                    if (nextPosition >= repo!!.stargazers.size) {
                        nextPosition = repo!!.stargazers.size - CHART_BARS_COUNT - 1
                    }
                }
            }
        }


        val result = repo!!.stargazers
            .slice(nextPosition .. nextPosition + CHART_BARS_COUNT)
        viewState.commitStargazers(
            StargazersChartHelper.getBars(
                result,
                groupType
            )
        )
        chartPageNumber++
    }

    fun prevChartPage() {
        var prevPosition = chartPageNumber * CHART_BARS_COUNT
        when (loadMoreAvailable) {
            ALLOW_LOAD_MORE -> {
                CoroutineScope(Dispatchers.Main).launch {
                    loadMoreStargazers(groupType)
                }
                if (prevPosition >= repo!!.stargazers.size) {
                    prevPosition = repo!!.stargazers.size - CHART_BARS_COUNT - 1
                }
            }

            DISMISS_LOAD_MORE -> {
                if (prevPosition >= repo!!.stargazers.size) {
                    prevPosition = repo!!.stargazers.size - CHART_BARS_COUNT - 1
                }
            }
        }
        for (i in prevPosition until prevPosition + CHART_BARS_COUNT)
            viewState.commitStargazers(
                StargazersChartHelper.getBars(
                    repo!!.stargazers,
                    groupType
                )
            )
    }

    private suspend fun loadMoreStargazers(groupType: GroupType) {
        when (loadMoreAvailable) {
            ALLOW_LOAD_MORE -> {
                val stargazersPart = findStargazers()
                val filteredStargazers = stargazersPart.filter {
                    repo!!.stargazers.none { r -> it.user.id == r.user.id }
                }

                val summaryStargazers = mutableListOf<Stargazer>()
                summaryStargazers.addAll(repo!!.stargazers)
                summaryStargazers.addAll(filteredStargazers)

                (repo as RepoBody).stargazers = (summaryStargazers)

                viewState.commitStargazers(
                    StargazersChartHelper.getBars(
                        repo!!.stargazers,
                        groupType
                    )
                )

                if (filteredStargazers.isEmpty()) {
                    loadMoreAvailable = DISMISS_LOAD_MORE
                }

                cacheRepository(repo)
            }
        }
    }

    private suspend fun cacheRepository(repo: Repo?) {
        if (repo != null) {
            repository.clearStargazers(repo.id)
            repo.stargazers.forEach {
                if (!repository.isUserExist(it.user.login)) repository.addUser(it.user)
                repository.addStargazer(it)
            }
        }
    }

    companion object {
        const val CHART_START_PAGE_NUMBER = 0
        const val START_PAGE_NUMBER = 1
        const val ALLOW_LOAD_MORE = 2
        const val DISMISS_LOAD_MORE = 3
        const val CHART_BARS_COUNT = 5
    }
}