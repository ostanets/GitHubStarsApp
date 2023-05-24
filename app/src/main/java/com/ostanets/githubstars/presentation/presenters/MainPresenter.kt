package com.ostanets.githubstars.presentation.presenters

import android.util.Log
import com.ostanets.githubstars.data.remote.github.GithubApiService
import com.ostanets.githubstars.data.toDomain
import com.ostanets.githubstars.di.DaggerGithubNetworkComponent
import com.ostanets.githubstars.domain.GithubRepository
import com.ostanets.githubstars.domain.GithubStarsAppRepository
import com.ostanets.githubstars.domain.GithubUser
import com.ostanets.githubstars.presentation.views.MainView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import moxy.InjectViewState
import moxy.MvpPresenter
import javax.inject.Inject

@InjectViewState
class MainPresenter(private val repository: GithubStarsAppRepository) : MvpPresenter<MainView>() {
    @Inject
    lateinit var githubApiService: GithubApiService

    init {
        DaggerGithubNetworkComponent.create().inject(this)
    }

    private var user: GithubUser? = null

    fun getRepositories(login: String) {
        if (!isValidLogin(login)) {
            viewState.showError("Invalid github user")
            return
        }

        CoroutineScope(Dispatchers.Main).launch {
            viewState.startSearch()

            val cacheDataDeferred = async(start = CoroutineStart.LAZY) { loadCachedData(login) }
            val networkDataDeferred = async(start = CoroutineStart.LAZY) {
                loadNetworkData(login)
            }

            cacheDataDeferred.start()
            networkDataDeferred.start()

            val cachedUser = cacheDataDeferred.await()
            if (cachedUser != null) {
                user = cachedUser
                user?.Repositories?.let {
                    viewState.commitRepositories(it)
                }
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
            } else {
                viewState.commitRepositories(emptyList())
                viewState.showError("User not found")
            }

            viewState.endSearch()

            cacheUser(newUser)
        }
    }

    private suspend fun initFavouriteStatuses() {
        user?.Repositories?.forEach {
            it.Favourite = repository.isRepositoryFavourite(it.Id)
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

    private suspend fun loadNetworkData(login: String): GithubUser? {
        return try {
            val user = findUser(login)
            val repositories = findRepositories(login, user)
            user.Repositories = repositories
            user
        } catch (_: Exception) {
            null
        }
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
}