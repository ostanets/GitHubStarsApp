package com.ostanets.githubstars.presentation.presenters

import com.ostanets.githubstars.data.remote.github.GithubApiService
import com.ostanets.githubstars.data.toDomain
import com.ostanets.githubstars.di.DaggerGithubNetworkComponent
import com.ostanets.githubstars.domain.GithubRepository
import com.ostanets.githubstars.domain.GithubStarsAppRepository
import com.ostanets.githubstars.domain.GithubUser
import com.ostanets.githubstars.presentation.views.MainView
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import moxy.InjectViewState
import moxy.MvpPresenter
import javax.inject.Inject

@InjectViewState
class MainPresenter(private val repository: GithubStarsAppRepository) : MvpPresenter<MainView>() {
    private val TAG = MainPresenter::class.simpleName

    @Inject
    lateinit var githubApiService: GithubApiService

    init {
        DaggerGithubNetworkComponent.create().inject(this)
    }

    private val user: GithubUser? = null

    fun getRepositories(login: String) {
        if (!isValidLogin(login)) {
            viewState.showError("Invalid github user")
            return
        }
        viewState.startSearch()

        runBlocking {
            val cashDataLoading = launch {
                val cashedUser = loadCashedData(login)
                user ?: cashedUser
            }

            val networkDataLoading = launch {
                loadNetworkData(login)
            }

            cashDataLoading.join()
            networkDataLoading.join()
        }

        viewState.showError("wtf")

        viewState.endSearch()
    }

    private suspend fun loadCashedData(login: String): GithubUser? {
        val user = repository.getUser(login)
        return user?.let { repository.initRepositories(it) }
    }

    private suspend fun loadNetworkData(login: String): GithubUser {
        val user = findUser(login)
        val repositories = findRepositories(login, user)

        val newUser = user.copy(Repositories = repositories)
        repository.addUser(newUser)
        return newUser
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