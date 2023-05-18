package com.ostanets.githubstars.domain

data class GithubRepository (
    val Id: Long,
    val Name: String,
    val Stargazers: List<GithubRepositoryStargazers>,
)