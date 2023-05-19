package com.ostanets.githubstars.data

import androidx.room.Entity
import androidx.room.ForeignKey
import java.time.LocalDateTime

@Entity(
    tableName = "github_repositories_stargazers",
    foreignKeys = [
        ForeignKey(
            entity = GithubUser::class,
            parentColumns = ["UserId"],
            childColumns = ["UserId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = GithubRepository::class,
            parentColumns = ["RepositoryId"],
            childColumns = ["RepositoryId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class GithubStargazer(
    val UserId: Long,
    val RepositoryId: Long,
    val StarredAt: LocalDateTime
)