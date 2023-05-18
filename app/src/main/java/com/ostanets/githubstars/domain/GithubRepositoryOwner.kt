package com.ostanets.githubstars.domain

data class GithubRepositoryOwner (
    val user: GithubUser,
    val Repositories: List<GithubRepository>,
)