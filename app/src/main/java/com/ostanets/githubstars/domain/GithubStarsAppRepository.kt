package com.ostanets.githubstars.domain

interface GithubStarsAppRepository {

    //ADD
    suspend fun addUser(user: GithubUser): Long

    suspend fun addRepository(repository: GithubRepository): Long

    suspend fun addStargazer(stargazer: GithubStargazer): Long

    //GET
    suspend fun getUser(userId: Long): GithubUser?

    suspend fun getUser(login: String): GithubUser?

    suspend fun isUserExist(login: String): Boolean

    suspend fun isRepositoryExist(repositoryId: Long): Boolean

    suspend fun isRepositoryFavorite(repositoryId: Long): Boolean

    suspend fun getFavorites(): List<GithubRepository>?

    suspend fun getFavorites(userId: Long): List<GithubRepository>?

    suspend fun initRepositories(user: GithubUser): GithubUser

    suspend fun initStargazers(repository: GithubRepository): GithubRepository

    //EDIT
    suspend fun editUser(user: GithubUser)

    suspend fun editRepository(repository: GithubRepository)

    suspend fun addRepositoryToFavorites(repositoryId: Long)

    suspend fun removeRepositoryFromFavorites(repositoryId: Long)

    //DELETE
    suspend fun deleteRepository(repositoryId: Long)
}