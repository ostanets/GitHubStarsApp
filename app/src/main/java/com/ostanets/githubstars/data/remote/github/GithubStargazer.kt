package com.ostanets.githubstars.data.remote.github

import org.threeten.bp.LocalDateTime

data class GithubStargazer(
    val UserId: Long,
    val StarredAt: LocalDateTime
)
