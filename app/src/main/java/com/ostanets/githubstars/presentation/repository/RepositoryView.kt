package com.ostanets.githubstars.presentation.repository

import com.ostanets.githubstars.domain.GithubStargazer
import moxy.MvpView
import moxy.viewstate.strategy.AddToEndSingleStrategy
import moxy.viewstate.strategy.SkipStrategy
import moxy.viewstate.strategy.StateStrategyType

@StateStrategyType(value = AddToEndSingleStrategy::class)
interface RepositoryView: MvpView {
    fun initializeData(state: String)
    fun commitStargazers(stars: List<GithubStargazer>)
    @StateStrategyType(value = SkipStrategy::class)
    fun showError(message: String)
}