package com.ostanets.githubstars.presentation.main

import com.ostanets.githubstars.domain.GithubRepository
import moxy.MvpView
import moxy.viewstate.strategy.AddToEndSingleStrategy
import moxy.viewstate.strategy.SkipStrategy
import moxy.viewstate.strategy.StateStrategyType

@StateStrategyType(value = AddToEndSingleStrategy::class)
interface MainView: MvpView {
    fun setSearchState(state: String)
    fun commitRepositories(repositories: List<GithubRepository>)
    @StateStrategyType(value = SkipStrategy::class)
    fun showError(message: String)

    companion object {
        const val START_SEARCH = "start"
        const val END_SEARCH = "end"
        const val CACHE_LOADED = "cache loaded"
    }
}