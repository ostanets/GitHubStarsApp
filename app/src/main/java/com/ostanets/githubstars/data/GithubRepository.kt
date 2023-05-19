package com.ostanets.githubstars.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "github_repositories",
    foreignKeys = [ForeignKey(
        entity = GithubUser::class,
        parentColumns = ["UserId"],
        childColumns = ["UserId"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class GithubRepository(
    @PrimaryKey
    val RepositoryId: Long,
    val Name: String,
    val Favourite: Boolean,
    @ColumnInfo(index = true)
    val UserId: Long
)