package com.ostanets.githubstars.data

import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(
    tableName = "favourite_repositories",
    primaryKeys = ["RepositoryId"],
    foreignKeys = [ForeignKey(
        entity = GithubRepository::class,
        parentColumns = ["RepositoryId"],
        childColumns = ["RepositoryId"],
        onDelete = ForeignKey.NO_ACTION
    )]
)
data class FavouriteRepository(
    val RepositoryId: Long
)