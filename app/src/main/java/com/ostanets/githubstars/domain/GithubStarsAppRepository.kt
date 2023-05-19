package com.ostanets.githubstars.domain

interface GithubStarsAppRepository {

    //ADD
    suspend fun addUser(user: GithubUser)

    suspend fun addRepository(repository: GithubRepository)

    suspend fun addStargazer(stargazer: GithubStargazer)

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