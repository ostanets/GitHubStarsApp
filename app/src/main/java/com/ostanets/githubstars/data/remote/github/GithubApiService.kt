package com.ostanets.githubstars.data.remote.github

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface GithubApiService {
    @GET("users/{user}")
    suspend fun findUser(
        @Path("user") login: String
    ): GithubUser

    @GET("users/{user}/repos")
    suspend fun listRepos(
        @Path("user") login: String,
        @Query("pageNumber") pageNumber: Int,
        @Query("per_page") limit: Int
    ): List<GithubRepository>

    @GET("{user}/{repo}/stargazers")
    suspend fun listStargazers(
        @Path("user") login: String,
        @Path("repo") repo: String,
        @Query("pageNumber") pageNumber: Int,
        @Query("per_page") limit: Int
    ): List<GithubRepository>

    companion object {
        const val MAXIMUM_LIMIT = 100
    }
}