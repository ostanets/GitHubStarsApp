package com.ostanets.githubstars.data.remote.github

import com.squareup.moshi.Json

data class GithubStargazer(
    @Json(name = "user") val User: GithubUser,
    @Json(name = "starred_at") val StarredAt: String
)
