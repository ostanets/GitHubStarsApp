package com.ostanets.githubstars.domain

import org.threeten.bp.LocalDateTime

data class GithubStargazer (
    val UserId: Long,
    val RepositoryId: Long,
    val StarredAt: LocalDateTime,

    val User: GithubUser? = null
)