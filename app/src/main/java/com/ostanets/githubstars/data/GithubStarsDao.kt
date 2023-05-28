package com.ostanets.githubstars.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface GithubStarsDao {

    //ADD
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun addUser(user: GithubUser): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addRepository(repository: GithubRepository): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addStargazer(stargazer: GithubStargazer): Long

    //GET
    @Query("SELECT * FROM `github_users` WHERE userId = :userId")
    suspend fun getUser(userId: Long): GithubUser?

    @Query("SELECT * FROM `github_users` WHERE UPPER(login) = UPPER(:login)")
    suspend fun getUser(login: String): GithubUser?

    @Query("SELECT * FROM `github_repositories` WHERE favorite = 1")
    suspend fun getFavorites(): List<GithubRepository>?

    @Query("SELECT * FROM `github_repositories` WHERE userId = :userId AND favorite = 1")
    suspend fun getFavorites(userId: Long): List<GithubRepository>?

    @Query("SELECT Favorite FROM `github_repositories` WHERE repositoryId = :repositoryId")
    suspend fun isRepositoryFavorite(repositoryId: Long): Boolean

    @Query("SELECT * FROM `github_repositories` WHERE repositoryId = :repositoryId")
    suspend fun getRepository(repositoryId: Long): GithubRepository?

    @Query("SELECT * FROM `github_repositories` WHERE userId = :userId")
    suspend fun getRepositories(userId: Long): List<GithubRepository>?

    @Query("SELECT * FROM `github_repositories_stargazers` WHERE repositoryId = :repositoryId")
    suspend fun getStargazers(repositoryId: Long): List<GithubStargazer>?

    //EDIT
    @Query("UPDATE `github_users` SET login = :login, avatarUrl = :avatarUrl WHERE userId = :userId")
    suspend fun editUser(userId: Long, login: String, avatarUrl: String)

    @Query("UPDATE `github_repositories` SET name = :name WHERE repositoryId = :repositoryId")
    suspend fun editRepository(repositoryId: Long, name: String)

    @Query("UPDATE `github_repositories` SET favorite = 1 WHERE repositoryId = :repositoryId")
    suspend fun addRepositoryToFavorites(repositoryId: Long)

    @Query("UPDATE `github_repositories` SET favorite = 0 WHERE repositoryId = :repositoryId")
    suspend fun removeRepositoryFromFavorites(repositoryId: Long)

    //DELETE
    @Query("DELETE FROM `github_repositories` WHERE repositoryId = :repositoryId")
    suspend fun deleteRepository(repositoryId: Long)
}