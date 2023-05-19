package com.ostanets.githubstars.presenters

import com.ostanets.githubstars.domain.GithubStarsAppRepository
import com.ostanets.githubstars.views.MainView
import moxy.InjectViewState
import moxy.MvpPresenter

@InjectViewState
class MainPresenter(private val repository: GithubStarsAppRepository) : MvpPresenter<MainView>() {

    fun getRepositories(login: String) {
        if (!isValidLogin(login)) {
            viewState.showError("Invalid github user")
            return
        }

        viewState.startSending()
        viewState.endSending()
    }

    private fun isValidLogin(login: String): Boolean {
        return login.isNotBlank()
    }
}