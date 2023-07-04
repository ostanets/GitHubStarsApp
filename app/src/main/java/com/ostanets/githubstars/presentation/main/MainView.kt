package com.ostanets.githubstars.presentation.main

import com.ostanets.githubstars.domain.Repo
import moxy.MvpView
import moxy.viewstate.strategy.AddToEndSingleStrategy
import moxy.viewstate.strategy.OneExecutionStateStrategy
import moxy.viewstate.strategy.StateStrategyType

@StateStrategyType(value = AddToEndSingleStrategy::class)
interface MainView: MvpView {
    fun setSearchState(state: String)
    fun commitRepositories(repositories: List<Repo>)
    @StateStrategyType(value = OneExecutionStateStrategy::class)
    fun showError(message: String)

    companion object {
        const val START_SEARCH = "start"
        const val END_SEARCH = "end"
        const val CACHE_LOADED = "cache loaded"
        const val LOAD_MORE_REPOSITORIES = "load more repos"
    }
}