package com.ostanets.githubstars.data.remote.github

import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path
import retrofit2.http.Query

interface GithubApiService {
    @GET("users/{user}")
    suspend fun getUser(
        @Path("user") login: String
    ): GithubUser

    @GET("users/{user}/repos")
    suspend fun listRepos(
        @Path("user") login: String,
        @Query("page") pageNumber: Int,
        @Query("per_page") limit: Int
    ): List<GithubRepository>

    @GET("repos/{user}/{repo}")
    suspend fun getRepo(
        @Path("user") login: String,
        @Path("repo") repository: String
    ): GithubRepository

    @Headers("Accept: application/vnd.github.star+json")
    @GET("repos/{user}/{repo}/stargazers")
    suspend fun listStargazers(
        @Path("user") login: String,
        @Path("repo") repo: String,
        @Query("page") pageNumber: Int,
        @Query("per_page") limit: Int
    ): List<GithubStargazer>

    companion object {
        const val MAXIMUM_PER_PAGE_LIMIT = 100
    }
}