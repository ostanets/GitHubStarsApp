package com.ostanets.githubstars.data.remote.github

import com.squareup.moshi.Json

data class GithubUser(
    @Json(name = "id") val Id: Long,
    @Json(name = "login") val Login: String,
    @Json(name = "avatar_url") val AvatarUrl: String
)
