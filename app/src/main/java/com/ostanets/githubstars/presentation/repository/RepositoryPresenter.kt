package com.ostanets.githubstars.presentation.repository

import com.ostanets.githubstars.data.remote.github.GithubApiService
import com.ostanets.githubstars.data.toDomain
import com.ostanets.githubstars.di.DaggerGithubNetworkComponent
import com.ostanets.githubstars.domain.GithubRepository
import com.ostanets.githubstars.domain.GithubStargazer
import com.ostanets.githubstars.domain.GithubStarsAppRepository
import com.ostanets.githubstars.domain.GithubUser
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
class RepositoryPresenter(private val repository: GithubStarsAppRepository) :
    MvpPresenter<RepositoryView>() {
    @Inject
    lateinit var githubApiService: GithubApiService

    init {
        DaggerGithubNetworkComponent.create().inject(this)
    }

    private var user: GithubUser? = null
    private var repo: GithubRepository? = null
    private var groupType = GroupType.DAILY
    private var chartPageNumber = CHART_START_PAGE_NUMBER
    private var pageNumber = START_PAGE_NUMBER
    private var loadMoreAvailable = ALLOW_LOAD_MORE

    fun getRepository(repositoryId: Long) {
        CoroutineScope(Dispatchers.Main).launch {
            repo = getCachedRepository(repositoryId)
            user = getCachedUser(repo!!.UserId)
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

    private suspend fun getCachedRepository(repositoryId: Long): GithubRepository {
        var cachedRepository = repository.getRepository(repositoryId)
            ?: throw Exception("User $repositoryId not found")
        cachedRepository = repository.initStargazers(cachedRepository)
        return cachedRepository
    }

    private suspend fun getCachedUser(userId: Long): GithubUser {
        return repository.getUser(userId) ?: throw Exception("User $userId not found")
    }

    private fun sendDataToActivity() {
        viewState.setOwner(user!!.Login)
        viewState.setRepository(repo!!.Name)
        viewState.setStarsCount(repo!!.Stargazers.size)
        nextChartPage()
        viewState.setFavorite(repo!!.Favorite)
        viewState.hideProgressBar()
    }

    private suspend fun getNetworkRepository(): GithubRepository {
        val networkRepository = githubApiService.getRepo(user!!.Login, repo!!.Name)
            .toDomain(user!!.Id)
        networkRepository.Stargazers = findStargazers()
        return networkRepository
    }

    private suspend fun findStargazers(): MutableList<GithubStargazer> {

        val stargazers: List<GithubStargazer> = githubApiService.listStargazers(
            user!!.Login,
            repo!!.Name,
            pageNumber++,
            GithubApiService.MAXIMUM_PER_PAGE_LIMIT
        ).map {
            it.toDomain(repo!!.Id)
        }

        return stargazers.toMutableList()
    }

    fun nextChartPage() {
        var nextPosition = chartPageNumber * CHART_BARS_COUNT
        if (nextPosition + CHART_BARS_COUNT >= repo!!.Stargazers.size) {
            when (loadMoreAvailable) {
                ALLOW_LOAD_MORE -> {
                    CoroutineScope(Dispatchers.Main).launch {
                        loadMoreStargazers(groupType)
                    }
                    if (nextPosition >= repo!!.Stargazers.size) {
                        nextPosition = repo!!.Stargazers.size - CHART_BARS_COUNT - 1
                    }
                }

                DISMISS_LOAD_MORE -> {
                    if (nextPosition >= repo!!.Stargazers.size) {
                        nextPosition = repo!!.Stargazers.size - CHART_BARS_COUNT - 1
                    }
                }
            }
        }


        val result = repo!!.Stargazers
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
                if (prevPosition >= repo!!.Stargazers.size) {
                    prevPosition = repo!!.Stargazers.size - CHART_BARS_COUNT - 1
                }
            }

            DISMISS_LOAD_MORE -> {
                if (prevPosition >= repo!!.Stargazers.size) {
                    prevPosition = repo!!.Stargazers.size - CHART_BARS_COUNT - 1
                }
            }
        }
        for (i in prevPosition until prevPosition + CHART_BARS_COUNT)
            viewState.commitStargazers(
                StargazersChartHelper.getBars(
                    repo!!.Stargazers,
                    groupType
                )
            )
    }

    private suspend fun loadMoreStargazers(groupType: GroupType) {
        when (loadMoreAvailable) {
            ALLOW_LOAD_MORE -> {
                val stargazersPart = findStargazers()
                val filteredStargazers = stargazersPart.filter {
                    repo!!.Stargazers.none { r -> it.User.Id == r.User.Id }
                }

                repo!!.Stargazers.addAll(filteredStargazers)
                viewState.commitStargazers(
                    StargazersChartHelper.getBars(
                        repo!!.Stargazers,
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

    private suspend fun cacheRepository(repo: GithubRepository?) {
        if (repo != null) {
            repository.clearStargazers(repo.Id)
            repo.Stargazers.forEach {
                if (!repository.isUserExist(it.User.Login)) repository.addUser(it.User)
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