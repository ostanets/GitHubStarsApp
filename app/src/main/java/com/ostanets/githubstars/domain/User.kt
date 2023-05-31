package com.ostanets.githubstars.domain

interface User {

    val id: Long

    val login: String

    val avatarUrl: String

    val repos: List<Repo>

}