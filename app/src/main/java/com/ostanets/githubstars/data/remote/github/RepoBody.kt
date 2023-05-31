package com.ostanets.githubstars.data.remote.github

import com.ostanets.githubstars.domain.Repo
import com.ostanets.githubstars.domain.Stargazer
import com.squareup.moshi.Json

data class RepoBody(
    @Json(name = "id") override val id: Long,
    @Json(name = "name") override val name: String,
) : Repo {
    override var favourite: Boolean? = null
    override var ownerId: Long? = null
    override lateinit var stargazers: List<Stargazer>
}
