package com.ostanets.githubstars.presentation.views

import moxy.MvpView
import moxy.viewstate.strategy.AddToEndSingleStrategy
import moxy.viewstate.strategy.SkipStrategy
import moxy.viewstate.strategy.StateStrategyType

@StateStrategyType(value = AddToEndSingleStrategy::class)
interface MainView: MvpView {
    fun startSearch()
    fun endSearch()
    fun commitRepositories()
    @StateStrategyType(value = SkipStrategy::class)
    fun showError(message: String)
}