package com.ostanets.githubstars.data.remote.github

import com.ostanets.githubstars.domain.GithubStargazer
import com.ostanets.githubstars.domain.Repo
import com.ostanets.githubstars.domain.Repo.Companion.UNDEFINED_ID
import com.squareup.moshi.Json

data class RepoBody(
    @Json(name = "id") override val id: Long,
    @Json(name = "name") override val name: String,
) : Repo {
    override var ownerId = UNDEFINED_ID
    override lateinit var stargazers: List<GithubStargazer>
}
