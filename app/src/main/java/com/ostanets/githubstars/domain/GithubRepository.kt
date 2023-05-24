package com.ostanets.githubstars.domain


data class GithubRepository(
    val Id: Long,
    var Name: String,
    var Favourite: Boolean,
    var UserId: Long,
    var Stargazers: List<GithubStargazer>? = null
)