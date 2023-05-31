package com.ostanets.githubstars.domain

interface Repo {

    val id: Long

    val name: String

    val favourite: Boolean?

    val ownerId: Long?

    val stargazers: List<Stargazer>

}