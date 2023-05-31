package com.ostanets.githubstars.presentation.repository

import moxy.MvpView
import moxy.viewstate.strategy.AddToEndSingleStrategy
import moxy.viewstate.strategy.SkipStrategy
import moxy.viewstate.strategy.StateStrategyType

@StateStrategyType(value = AddToEndSingleStrategy::class)
interface RepositoryView: MvpView {
    fun setOwner(name: String)
    fun setRepository(name: String)
    fun setStarsCount(count: Int)
    fun setFavorite(status: Boolean)
    fun commitStargazers(stars: List<StargazersBar>)
    fun hideProgressBar()
    @StateStrategyType(value = SkipStrategy::class)
    fun showError(message: String)
}