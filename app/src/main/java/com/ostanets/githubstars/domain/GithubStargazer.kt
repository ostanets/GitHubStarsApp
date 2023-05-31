package com.ostanets.githubstars.domain

import org.threeten.bp.LocalDateTime

data class GithubStargazer (
    val User: GithubUser,
    val RepositoryId: Long,
    val StarredAt: LocalDateTime
)