package com.ostanets.githubstars.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface GithubStarsDao {

    //ADD
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addUser(user: GithubUser)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addRepository(repository: GithubRepository)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addStargazer(stargazer: GithubStargazer)

    //GET
    @Query("SELECT * FROM `github_users` WHERE login = :login")
    suspend fun getUser(login: String): GithubUser

    @Query("SELECT * FROM `github_repositories` WHERE favourite = 1")
    suspend fun getFavourites()

    @Query("SELECT * FROM `github_repositories` WHERE userId = :userId")
    suspend fun initRepositories(userId: Long): GithubUser

    @Query("SELECT * FROM `github_repositories_stargazers` WHERE repositoryId = :repositoryId")
    suspend fun initStargazers(repositoryId: Long): GithubRepository

    //EDIT
    @Query("UPDATE `github_repositories` SET favourite = 1 WHERE repositoryId = :repositoryId")
    suspend fun addRepositoryToFavourites(repositoryId: Long)

    @Query("UPDATE `github_repositories` SET favourite = 0 WHERE repositoryId = :repositoryId")
    suspend fun removeRepositoryFromFavourites(repositoryId: Long)
}