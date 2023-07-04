package com.ostanets.githubstars.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface AppDao {

    //ADD
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun addUser(user: UserBody): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addRepo(repository: RepoBody): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addStargazer(stargazer: StargazerBody): Long

    //GET
    @Query("SELECT * FROM `github_users` WHERE id = :userId")
    suspend fun getUser(userId: Long): UserBody?

    @Query("SELECT * FROM `github_users` WHERE UPPER(login) = UPPER(:login)")
    suspend fun getUser(login: String): UserBody?

    @Query("SELECT * FROM `github_repositories` WHERE favourite = 1")
    suspend fun getFavorites(): List<RepoBody>?

    @Query("SELECT * FROM `github_repositories` WHERE id = :userId AND favourite = 1")
    suspend fun getFavorites(userId: Long): List<RepoBody>?

    @Query("SELECT favourite FROM `github_repositories` WHERE id = :repositoryId")
    suspend fun isRepoFavorite(repositoryId: Long): Boolean

    @Query("SELECT * FROM `github_repositories` WHERE id = :repositoryId")
    suspend fun getRepo(repositoryId: Long): RepoBody?

    @Query("SELECT * FROM `github_repositories` WHERE id = :userId")
    suspend fun getRepos(userId: Long): List<RepoBody>?

    @Query("SELECT * FROM `github_repositories_stargazers` WHERE id = :repositoryId")
    suspend fun getStargazers(repositoryId: Long): List<StargazerBody>?

    //EDIT
    @Query("UPDATE `github_users` SET login = :login, avatarUrl = :avatarUrl WHERE id = :userId")
    suspend fun editUser(userId: Long, login: String, avatarUrl: String)

    @Query("UPDATE `github_repositories` SET name = :name WHERE id = :repositoryId")
    suspend fun editRepo(repositoryId: Long, name: String)

    @Query("UPDATE `github_repositories` SET favourite = 1 WHERE id = :repositoryId")
    suspend fun addRepoToFavorites(repositoryId: Long)

    @Query("UPDATE `github_repositories` SET favourite = 0 WHERE id = :repositoryId")
    suspend fun removeRepoFromFavorites(repositoryId: Long)

    //DELETE
    @Query("DELETE FROM `github_repositories` WHERE id = :repositoryId")
    suspend fun deleteRepo(repositoryId: Long)

    @Query("DELETE FROM `github_repositories_stargazers` WHERE id = :repositoryId")
    suspend fun clearStargazers(repositoryId: Long)
}