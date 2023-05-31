package com.ostanets.githubstars.data.remote.github

import com.ostanets.githubstars.domain.Stargazer
import com.ostanets.githubstars.domain.User
import com.squareup.moshi.Json
import org.threeten.bp.LocalDateTime
import org.threeten.bp.format.DateTimeFormatter

data class StargazerBody(
    @Json(name = "user") val userBody: UserBody,
    @Json(name = "starred_at") val starredAtString: String

) : Stargazer {
    override val user: User
        get() = userBody
    override val repoId: Long? = null
    override val starredAt: LocalDateTime
        get() = LocalDateTime.parse(starredAtString, DateTimeFormatter.ISO_DATE_TIME)
}