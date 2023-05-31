package com.ostanets.githubstars.domain

interface GithubStarsAppRepo {

    //ADD
    suspend fun addUser(user: User): Long

    suspend fun addRepo(repository: Repo): Long

    suspend fun addStargazer(stargazer: Stargazer): Long

    //GET
    suspend fun getUser(userId: Long): User?

    suspend fun getUser(login: String): User?

    suspend fun getRepo(repositoryId: Long): Repo?

    suspend fun isUserExist(login: String): Boolean

    suspend fun isRepoExist(repositoryId: Long): Boolean

    suspend fun isRepoFavorite(repositoryId: Long): Boolean

    suspend fun getFavorites(): List<Repo>?

    suspend fun getFavorites(userId: Long): List<Repo>?

    suspend fun initRepos(user: User): User

    suspend fun initStargazers(repository: Repo): Repo

    //EDIT
    suspend fun editUser(user: User)

    suspend fun editRepository(repository: Repo)

    suspend fun addRepositoryToFavorites(repositoryId: Long)

    suspend fun removeRepoFromFavorites(repositoryId: Long)

    //DELETE
    suspend fun deleteRepo(repositoryId: Long)

    suspend fun clearStargazers(repositoryId: Long)
}