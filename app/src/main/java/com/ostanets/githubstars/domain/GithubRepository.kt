package com.ostanets.githubstars.domain

import java.time.LocalDateTime
import java.util.SortedMap

data class GithubRepository(
    val Id: Long,
    val Name: String,
    val Favourite: Boolean,
    val Stargazers: SortedMap<LocalDateTime, GithubUser>? = null
)