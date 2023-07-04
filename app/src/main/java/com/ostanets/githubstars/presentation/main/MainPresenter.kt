package com.ostanets.githubstars.presentation.main

import android.util.Log
import com.ostanets.githubstars.data.UserBody
import com.ostanets.githubstars.data.remote.github.GithubApiService
import com.ostanets.githubstars.di.DaggerGithubNetworkComponent
import com.ostanets.githubstars.domain.AppRepo
import com.ostanets.githubstars.domain.Repo
import com.ostanets.githubstars.domain.User
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
class MainPresenter(private val repository: AppRepo) : MvpPresenter<MainView>() {
    @Inject
    lateinit var githubApiService: GithubApiService

    init {
        DaggerGithubNetworkComponent.create().inject(this)
    }

    private var user: User? = null
    private var pageNumber = START_PAGE_NUMBER
    private var loadMoreAvailable = ALLOW_LOAD_MORE

    fun getRepositories(inputLogin: String) {
        val login = parseLogin(inputLogin)

        if (!isValidLogin(login)) {
            viewState.showError("Invalid github user")
            return
        }

        CoroutineScope(Dispatchers.Main).launch {
            viewState.setSearchState(MainView.START_SEARCH)

            pageNumber = START_PAGE_NUMBER
            loadMoreAvailable = ALLOW_LOAD_MORE

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
                user?.repos?.let {
                    viewState.commitRepositories(it)
                    viewState.setSearchState(MainView.CACHE_LOADED)
                }
            } else {
                viewState.commitRepositories(emptyList())
            }

            val newUser = networkDataDeferred.await()
            if (newUser != null) {
                user = newUser
                val repos = user!!.repos.toMutableList()
                val updatedFavorites = getFavorites()

                for (updatedRepo in updatedFavorites) {
                    val existingRepo = repos.find { it.id == updatedRepo.id }

                    if (existingRepo != null) {
                        val index = repos.indexOf(existingRepo)
                        repos[index] = updatedRepo
                    } else {
                        repos.add(updatedRepo)
                    }
                }

                Log.d("TAG", "getRepositories: $repos")
                if (repos.isNotEmpty()) {
                    viewState.commitRepositories(repos)
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

    fun toggleLike(repo: Repo) {
        CoroutineScope(Dispatchers.Main).launch {
            if (repo.favourite == true) {
                repository.removeRepoFromFavorites(repo.id)
            } else {
                repository.addRepoToFavorites(repo.id)
            }

            user = repo.ownerId?.let { repository.getUser(it) }
            user?.repos?.let { viewState.commitRepositories(it) }
        }
    }

    private suspend fun loadCachedData(login: String): User? {
        val user = repository.getUser(login)
        return user?.let { repository.initRepos(it) }
    }

    private suspend fun loadNetworkData(login: String): User {
        val user = findUser(login)

        val repositoriesPart = findRepositories(login)

        val summaryRepos = mutableListOf<Repo>()
        summaryRepos.addAll(user.repos)
        summaryRepos.addAll(repositoriesPart)

        (user as UserBody).repos = summaryRepos
        return user
    }

    fun loadMoreRepositories() {
        CoroutineScope(Dispatchers.Main).launch {
            when (loadMoreAvailable) {
                ALLOW_LOAD_MORE -> {
                    user ?: throw Exception("User is not initialized")

                    viewState.setSearchState(MainView.LOAD_MORE_REPOSITORIES)
                    val login = user!!.login
                    val repositoriesPart = findRepositories(login)

                    val filteredRepositories = repositoriesPart.filter {
                        user!!.repos.none { r -> it.id == r.id }
                    }

                    val summaryRepos = mutableListOf<Repo>()
                    summaryRepos.addAll(user!!.repos)
                    summaryRepos.addAll(filteredRepositories)

                    (user as UserBody).repos = summaryRepos
                    viewState.commitRepositories(user!!.repos)
                    viewState.setSearchState(MainView.END_SEARCH)

                    if (filteredRepositories.isEmpty()) {
                        loadMoreAvailable = DISMISS_LOAD_MORE
                    }

                    cacheUser(user)
                }
            }
        }
    }

    private suspend fun getFavorites(): MutableList<Repo> {
        user ?: throw Exception("User is not initialized")
        val login = user!!.login
        val userId = user!!.id
        val favorites = repository.getFavorites(userId)
        favorites ?: return mutableListOf()
        val result = mutableListOf<Repo>()
        favorites.forEach {
            try {
                val repo = githubApiService.getRepo(login, it.name)
                repo.favourite = true
                result.add(repo)
            } catch (e: HttpException) {
                if (e.code() == 404) {
                    viewState.showError("Favorite repository ${it.name} is not longer available")
                    repository.deleteRepo(it.id)
                } else {
                    viewState.showError("Unexpected HTTP Exception")
                }
            }
        }
        return result
    }

    private suspend fun cacheUser(user: User?) {
        if (user != null) {
            if (repository.isUserExist(user.login)) {
                repository.editUser(user)
                val repositoriesCopy = user.repos.toList()
                repositoriesCopy.forEach {
                    repository.addRepo(it)
                }
            } else {
                repository.addUser(user)
            }
        }
    }

    private suspend fun findRepositories(
        login: String
    ): MutableList<Repo> {

        val repositories: List<Repo> = githubApiService.listRepos(
            login,
            pageNumber++,
            GithubApiService.MAXIMUM_PER_PAGE_LIMIT
        )

        return repositories.toMutableList()
    }

    private suspend fun findUser(login: String): User {
        return githubApiService.getUser(login)
    }

    private fun isValidLogin(login: String): Boolean {
        return login.isNotBlank()
    }

    private fun parseLogin(login: String): String {
        return login.trim()
    }

    companion object {
        const val START_PAGE_NUMBER = 1
        const val ALLOW_LOAD_MORE = 2
        const val DISMISS_LOAD_MORE = 3
    }
}