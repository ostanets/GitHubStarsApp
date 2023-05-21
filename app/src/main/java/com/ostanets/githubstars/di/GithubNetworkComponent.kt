package com.ostanets.githubstars.di

import com.ostanets.githubstars.presentation.presenters.MainPresenter
import dagger.Component

@Component(modules = [GithubNetworkModule::class])
interface GithubNetworkComponent {
    fun inject(target: MainPresenter)
}