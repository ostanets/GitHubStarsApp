package com.ostanets.githubstars.data.remote.github

import retrofit2.http.GET
import retrofit2.http.Path

interface GithubApiService {
    @GET("users/{user}")
    suspend fun findUser(
        @Path("user") login: String
    ): GithubUser

    @GET("users/{user}/repos?per_page=100&page={pageNumber}")
    suspend fun listRepos(
        @Path("user") user: String,
        @Path("repo") repo: String,
        @Path("pageNumber") pageNumber: Int
    ): List<GithubRepository>

    @GET("{user}/{repo}/stargazers?per_page=100&page={pageNumber}")
    suspend fun listStargazers(
        @Path("user") user: String,
        @Path("repo") repo: String,
        @Path("pageNumber") pageNumber: Int
    ): List<GithubRepository>
}