package com.ostanets.githubstars.domain

interface Repo {

    val id: Long

    val name: String

    val ownerId: Long

    val stargazers: List<GithubStargazer>

    companion object {
        const val UNDEFINED_ID = -1L
    }
}