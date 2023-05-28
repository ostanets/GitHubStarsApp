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
                user?.Repositories?.let {
                    viewState.commitRepositories(it)
                }
            } else {
                viewState.commitRepositories(emptyList())
            }

            val newUser = networkDataDeferred.await()
            if (newUser != null) {
                user = newUser
                val repositories = user!!.Repositories
                val updatedFavorites = getFavorites()

                for (updatedRepo in updatedFavorites) {
                    val existingRepo = repositories.find { it.Id == updatedRepo.Id }

                    if (existingRepo != null) {
                        val index = repositories.indexOf(existingRepo)
                        repositories[index] = updatedRepo
                    } else {
                        repositories.add(updatedRepo)
                    }
                }

                Log.d("TAG", "getRepositories: $repositories")
                if (repositories.isNotEmpty()) {
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

    @Deprecated("Don't use because may not update all of favorites")
    private suspend fun initFavoriteStatuses() {
        user?.Repositories?.forEach {
            val favoriteStatus =
                try {
                    repository.isRepositoryFavorite(it.Id)
                } catch (e: NullPointerException) {
                    false
                }

            it.Favorite = favoriteStatus
        }
    }

    fun toggleLike(githubRepository: GithubRepository) {
        CoroutineScope(Dispatchers.Main).launch {
            if (githubRepository.Favorite) {
                repository.removeRepositoryFromFavorites(githubRepository.Id)
            } else {
                repository.addRepositoryToFavorites(githubRepository.Id)
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

        val repositoriesPart = findRepositories(login, user)
        user.Repositories.addAll(repositoriesPart)
        return user
    }

    fun loadMoreRepositories() {
        CoroutineScope(Dispatchers.Main).launch {
            when (loadMoreAvailable) {
                ALLOW_LOAD_MORE -> {
                    user ?: throw Exception("User is not initialized")
                    val login = user!!.Login
                    val repositoriesPart = findRepositories(login, user!!)

                    val filteredRepositories = repositoriesPart.filter {
                        user!!.Repositories.none { r -> it.Id == r.Id }
                    }
                    user!!.Repositories.addAll(filteredRepositories)
                    viewState.commitRepositories(user!!.Repositories)

                    if (filteredRepositories.isEmpty()) {
                        loadMoreAvailable = DISMISS_LOAD_MORE
                    }

                    Log.d("TAG", "loadMoreRepositories: 1: ${user!!.Repositories.size}")
                    cacheUser(user)
                    Log.d("TAG", "loadMoreRepositories: 2: ${user!!.Repositories.size}")
                }
            }
        }
    }

    private suspend fun getFavorites(): MutableList<GithubRepository> {
        user ?: throw Exception("User is not initialized")
        val login = user!!.Login
        val userId = user!!.Id
        val favorites = repository.getFavorites(userId)
        favorites ?: return mutableListOf()
        val result = mutableListOf<GithubRepository>()
        favorites.forEach {
            try {
                val repo = githubApiService.getRepo(login, it.Name).toDomain(userId)
                repo.Favorite = true
                result.add(repo)
            } catch (e: HttpException) {
                if (e.code() == 404) {
                    viewState.showError("Favorite repository ${it.Name} is not longer available")
                    repository.deleteRepository(it.Id)
                } else {
                    viewState.showError("Unexpected HTTP Exception")
                }
            }
        }
        return result
    }

    private suspend fun cacheUser(user: GithubUser?) {
        if (user != null) {
            if (repository.isUserExist(user.Login)) {
                repository.editUser(user)
                val repositoriesCopy = user.Repositories.toList()
                repositoriesCopy.forEach {
                    repository.addRepository(it)
                }
            } else {
                repository.addUser(user)
            }
        }
    }

    private suspend fun findRepositories(
        login: String,
        user: GithubUser,
    ): MutableList<GithubRepository> {

        val repositories: List<GithubRepository> = githubApiService.listRepos(
            login,
            pageNumber++,
            GithubApiService.MAXIMUM_PER_PAGE_LIMIT
        ).map {
            it.toDomain(user.Id)
        }

        return repositories.toMutableList()
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

    companion object {
        const val START_PAGE_NUMBER = 1
        const val ALLOW_LOAD_MORE = 2
        const val DISMISS_LOAD_MORE = 3
    }
}