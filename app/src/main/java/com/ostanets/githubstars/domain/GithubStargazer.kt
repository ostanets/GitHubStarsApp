package com.ostanets.githubstars.domain

import java.time.LocalDateTime

data class GithubStargazer (
    val UserId: Long,
    val RepositoryId: Long,
    val StarredAt: LocalDateTime,

    val User: GithubUser? = null
)