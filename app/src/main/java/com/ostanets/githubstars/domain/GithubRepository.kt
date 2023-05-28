package com.ostanets.githubstars.domain


data class GithubRepository(
    val Id: Long,
    var Name: String,
    var Favorite: Boolean,
    var UserId: Long,
    var Stargazers: List<GithubStargazer>? = null
)