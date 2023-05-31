package com.ostanets.githubstars.data.remote.github

import com.ostanets.githubstars.domain.Repo
import com.ostanets.githubstars.domain.User
import com.squareup.moshi.Json

data class UserBody(
    @Json(name = "id") override val id: Long,
    @Json(name = "login") override val login: String,
    @Json(name = "avatar_url") override val avatarUrl: String
) : User {
    override lateinit var repos: List<Repo>
}