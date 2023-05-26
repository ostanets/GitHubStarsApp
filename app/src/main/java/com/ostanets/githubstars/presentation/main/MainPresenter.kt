package com.ostanets.githubstars.presentation.main

import android.util.Log
import com.ostanets.githubstars.data.remote.github.GithubApiService
import com.ostanets.githubstars.data.toDomain
import com.ostanets.githubstars.di.DaggerGithubNetworkComponent
import com.ostanets.githubstars.domain.GithubRepository
import com.ostanets.githubstars.domain.GithubStarsAppRepository
import com.ostanets.githubstars.domain.GithubUser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import moxy.InjectViewState
import moxy.MvpPresenter
import retrofit2.HttpException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.inject.Inject

@InjectViewState
class MainPresenter(private val repository: GithubStarsAppRepository) : MvpPresenter<MainView>() {
    @Inject
    lateinit var githubApiService: GithubApiService

    init {
        DaggerGithubNetworkComponent.create().inject(this)
    }

    private var user: GithubUser? = null

    fun getRepositories(inputLogin: String) {
        val login = parseLogin(inputLogin)

        if (!isValidLogin(login)) {
            viewState.showError("Invalid github user")
            return
        }

        CoroutineScope(Dispatchers.Main).launch {
            viewState.setSearchState(MainView.START_SEARCH)

            val cacheDataDeferred = async(start = CoroutineStart.LAZY) { loadCachedData(login) }
            val networkDataDeferred = async(start = CoroutineStart.LAZY) {
                try {
                    loadNetworkData(login)
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
            }

            cacheDataDeferred.start()
            networkDataDeferred.start()

            val cachedUser = cacheDataDeferred.await()
            if (cachedUser != null) {
                user = cachedUser
                user?.Repositories?.let {
                    viewState.commitRepositories(it)
                }
            } else {
                viewState.commitRepositories(emptyList())
            }

            val newUser = networkDataDeferred.await()
            if (newUser != null) {
                user = newUser
                initFavouriteStatuses()
                val repositories = user?.Repositories
                Log.d("TAG", "getRepositories: ${repositories.toString()}")
                if (!repositories.isNullOrEmpty()) {
                    viewState.commitRepositories(repositories)
                } else {
                    viewState.commitRepositories(emptyList())
                    viewState.showError("User hasn't public repositories")
                }
            }

            viewState.setSearchState(MainView.END_SEARCH)

            cacheUser(newUser)
        }
    }

    private fun handleNoInternetConnection() {
        viewState.showError("No Internet connection, cache loaded")
    }

    private fun handleHttpException(e: HttpException) {
        if (e.code() == 404) {
            viewState.commitRepositories(emptyList())
            viewState.showError("User not found")
        } else {
            viewState.showError("Unexpected HTTP Exception")
        }
    }

    private suspend fun initFavouriteStatuses() {
        user?.Repositories?.forEach {
            val favouriteStatus =
                try {
                    repository.isRepositoryFavourite(it.Id)
                } catch (e: NullPointerException) {
                    false
                }

            it.Favourite = favouriteStatus
        }
    }

    fun toggleLike(githubRepository: GithubRepository) {
        CoroutineScope(Dispatchers.Main).launch {
            if (githubRepository.Favourite) {
                repository.removeRepositoryFromFavourites(githubRepository.Id)
            } else {
                repository.addRepositoryToFavourites(githubRepository.Id)
            }

            user = repository.getUser(githubRepository.UserId)

            user?.Repositories?.let { viewState.commitRepositories(it) }
        }
    }

    private suspend fun loadCachedData(login: String): GithubUser? {
        val user = repository.getUser(login)
        return user?.let { repository.initRepositories(it) }
    }

    private suspend fun loadNetworkData(login: String): GithubUser {
        val user = findUser(login)
        val repositories = findRepositories(login, user)
        user.Repositories = repositories
        return user
    }

    private suspend fun cacheUser(user: GithubUser?) {
        if (user != null) {
            if (repository.isUserExist(user.Login)) {
                repository.editUser(user)
                user.Repositories?.forEach {
                    if (repository.isRepositoryExist(it.Id)) {
                        repository.editRepository(it)
                    } else {
                        repository.addRepository(it)
                    }
                }
            } else {
                repository.addUser(user)
            }
        }
    }

    private suspend fun findRepositories(
        login: String,
        user: GithubUser,
    ): ArrayList<GithubRepository> {
        val repositories = ArrayList<GithubRepository>()

        var page = 1
        var repositoriesOnPage: List<GithubRepository>
        do {
            repositoriesOnPage = githubApiService.listRepos(
                login,
                page++,
                GithubApiService.MAXIMUM_LIMIT
            ).map {
                it.toDomain(user.Id)
            }
            repositories.addAll(repositoriesOnPage)
        } while (repositoriesOnPage.size == 100)
        return repositories
    }

    private suspend fun findUser(login: String): GithubUser {
        return githubApiService.findUser(login).toDomain()
    }

    private fun isValidLogin(login: String): Boolean {
        return login.isNotBlank()
    }

    private fun parseLogin(login: String): String {
        return login.trim()
    }
}