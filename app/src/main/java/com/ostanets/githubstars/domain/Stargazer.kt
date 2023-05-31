package com.ostanets.githubstars.domain

import org.threeten.bp.LocalDateTime

interface Stargazer {

    val user: User

    val repoId: Long?

    val starredAt: LocalDateTime

}