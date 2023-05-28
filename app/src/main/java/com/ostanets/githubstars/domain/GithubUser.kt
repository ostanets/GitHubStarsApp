package com.ostanets.githubstars.domain

data class GithubUser (
    val Id: Long,
    var Login: String,
    var AvatarUrl: String,
    var Repositories: MutableList<GithubRepository> = mutableListOf()
)