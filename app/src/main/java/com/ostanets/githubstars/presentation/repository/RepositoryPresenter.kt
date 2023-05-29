package com.ostanets.githubstars.presentation.repository

import com.ostanets.githubstars.data.remote.github.GithubApiService
import com.ostanets.githubstars.di.DaggerGithubNetworkComponent
import com.ostanets.githubstars.domain.GithubStarsAppRepository
import moxy.InjectViewState
import moxy.MvpPresenter
import javax.inject.Inject

@InjectViewState
class RepositoryPresenter(private val repository: GithubStarsAppRepository) : MvpPresenter<RepositoryView>() {
    @Inject
    lateinit var githubApiService: GithubApiService

    init {
        DaggerGithubNetworkComponent.create().inject(this)
    }
}