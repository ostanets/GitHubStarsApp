package com.ostanets.githubstars.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface GithubStarsDao {

    //ADD
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun addUser(user: GithubUser): Long

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun addRepository(repository: GithubRepository): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addStargazer(stargazer: GithubStargazer): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addRepositoryToFavourites(favouriteRepository: FavouriteRepository)

    //GET
    @Query("SELECT * FROM `github_users` WHERE userId = :userId")
    suspend fun getUser(userId: Long): GithubUser?

    @Query("SELECT * FROM `github_users` WHERE UPPER(login) = UPPER(:login)")
    suspend fun getUser(login: String): GithubUser?

    @Query("SELECT RepositoryId FROM `favourite_repositories`")
    suspend fun getFavourites(): List<Long>

    @Query("SELECT Favourite FROM `github_repositories` WHERE repositoryId = :repositoryId")
    suspend fun isRepositoryFavourite(repositoryId: Long): Boolean

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

    @Query("UPDATE `github_repositories` SET favourite = 1 WHERE repositoryId = :repositoryId")
    suspend fun addRepositoryToFavourites(repositoryId: Long)

    //REMOVE
    @Delete
    suspend fun removeRepositoryFromFavourites(favouriteRepository: FavouriteRepository)
}