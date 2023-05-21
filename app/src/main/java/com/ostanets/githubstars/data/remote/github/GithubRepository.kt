package com.ostanets.githubstars.data.remote.github

import com.squareup.moshi.Json

data class GithubRepository(
    @Json(name = "id") val Id: Long,
    @Json(name = "name") val Name: String
)
