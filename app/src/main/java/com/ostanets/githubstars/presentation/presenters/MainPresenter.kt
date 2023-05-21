package com.ostanets.githubstars.presentation.presenters

import android.util.Log
import com.ostanets.githubstars.data.remote.github.GithubApiService
import com.ostanets.githubstars.di.DaggerGithubNetworkComponent
import com.ostanets.githubstars.domain.GithubStarsAppRepository
import com.ostanets.githubstars.presentation.views.MainView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
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

    fun getRepositories(login: String) {
        if (!isValidLogin(login)) {
            viewState.showError("Invalid github user")
            return
        }

        CoroutineScope(Dispatchers.Main).launch {
            viewState.startSending()
            val user = githubApiService.findUser(login)
            Log.d(TAG, "getRepositories: $user")
            viewState.endSending()
        }
    }

    private fun isValidLogin(login: String): Boolean {
        return login.isNotBlank()
    }
}