package com.ostanets.githubstars.di

import com.ostanets.githubstars.presentation.main.MainPresenter
import com.ostanets.githubstars.presentation.repository.RepositoryPresenter
import dagger.Component

@Component(modules = [GithubNetworkModule::class])
interface GithubNetworkComponent {
    fun inject(target: MainPresenter)
    fun inject(target: RepositoryPresenter)
}