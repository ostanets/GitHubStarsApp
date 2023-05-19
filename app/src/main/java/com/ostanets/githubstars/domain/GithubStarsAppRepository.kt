package com.ostanets.githubstars.domain

import java.time.LocalDateTime

interface GithubStarsAppRepository {

    //ADD
    suspend fun addUser(user: GithubUser)

    suspend fun addRepository(owner: Long, repository: GithubRepository)

    suspend fun addStargazer(
        repository: Long,
        user: Long,
        starredAt: LocalDateTime
    )

    //GET
    suspend fun getUser(login: String): GithubUser

    suspend fun getFavourites()

    suspend fun initRepositories(user: GithubUser, page: Int): GithubUser

    suspend fun initStargazers(repository: GithubRepository, page: Int): GithubRepository

    //EDIT
    suspend fun addRepositoryToFavourites(repository: GithubRepository)

    suspend fun removeRepositoryFromFavourites(repository: GithubRepository)
}