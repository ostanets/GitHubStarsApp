package com.ostanets.githubstars.domain

interface GithubStarsAppRepository {

    //ADD
    suspend fun addUser(user: GithubUser): Long

    suspend fun addRepository(repository: GithubRepository): Long

    suspend fun addStargazer(stargazer: GithubStargazer): Long

    //GET
    suspend fun getUser(userId: Long): GithubUser

    suspend fun getUser(login: String): GithubUser

    suspend fun getFavourites(): List<GithubRepository>

    suspend fun initRepositories(user: GithubUser): GithubUser

    suspend fun initStargazers(repository: GithubRepository): GithubRepository

    //EDIT
    suspend fun addRepositoryToFavourites(repositoryId: Long)

    suspend fun removeRepositoryFromFavourites(repositoryId: Long)
}