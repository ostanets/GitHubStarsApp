package com.ostanets.githubstars.domain


data class GithubRepository(
    val Id: Long,
    val Name: String,
    val Favourite: Boolean,
    val UserId: Long,
    val Stargazers: List<GithubStargazer>? = null
)