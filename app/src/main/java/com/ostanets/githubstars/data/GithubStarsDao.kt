package com.ostanets.githubstars.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface GithubStarsDao {

    //ADD
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addUser(user: GithubUser): Long

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun addRepository(repository: GithubRepository): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addStargazer(stargazer: GithubStargazer): Long

    //GET
    @Query("SELECT * FROM `github_users` WHERE userId = :userId")
    suspend fun getUser(userId: Long): GithubUser

    @Query("SELECT * FROM `github_users` WHERE login = :login")
    suspend fun getUser(login: String): GithubUser

    @Query("SELECT * FROM `github_repositories` WHERE favourite = 1")
    suspend fun getFavourites(): List<GithubRepository>

    @Query("SELECT * FROM `github_repositories` WHERE repositoryId = :repositoryId")
    suspend fun getRepository(repositoryId: Long): GithubRepository

    @Query("SELECT * FROM `github_repositories` WHERE userId = :userId")
    suspend fun getRepositories(userId: Long): List<GithubRepository>

    @Query("SELECT * FROM `github_repositories_stargazers` WHERE repositoryId = :repositoryId")
    suspend fun getStargazers(repositoryId: Long): List<GithubStargazer>

    //EDIT
    @Query("UPDATE `github_repositories` SET favourite = 1 WHERE repositoryId = :repositoryId")
    suspend fun addRepositoryToFavourites(repositoryId: Long)

    @Query("UPDATE `github_repositories` SET favourite = 0 WHERE repositoryId = :repositoryId")
    suspend fun removeRepositoryFromFavourites(repositoryId: Long)
}