package com.ostanets.githubstars.domain

import java.time.LocalDateTime

data class GithubRepositoryStargazers (
    val User: GithubUser,
    val StarredAt: LocalDateTime
)