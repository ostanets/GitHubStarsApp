package com.ostanets.githubstars.domain

interface AppRepo {

    //ADD
    suspend fun addUser(user: User): Long

    suspend fun addRepo(repo: Repo): Long

    suspend fun addStargazer(stargazer: Stargazer): Long

    //GET
    suspend fun getUser(userId: Long): User?

    suspend fun getUser(login: String): User?

    suspend fun getRepo(repoId: Long): Repo?

    suspend fun isUserExist(login: String): Boolean

    suspend fun isRepoExist(repoId: Long): Boolean

    suspend fun isRepoFavorite(repoId: Long): Boolean

    suspend fun getFavorites(): List<Repo>?

    suspend fun getFavorites(userId: Long): List<Repo>?

    suspend fun initRepos(user: User): User

    suspend fun initStargazers(repo: Repo): Repo

    //EDIT
    suspend fun editUser(user: User)

    suspend fun editRepo(repo: Repo)

    suspend fun addRepoToFavorites(repoId: Long)

    suspend fun removeRepoFromFavorites(repoId: Long)

    //DELETE
    suspend fun deleteRepo(repoId: Long)

    suspend fun clearStargazers(repoId: Long)
}