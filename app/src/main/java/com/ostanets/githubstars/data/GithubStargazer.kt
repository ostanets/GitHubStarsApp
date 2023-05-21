package com.ostanets.githubstars.data

import androidx.room.Entity
import androidx.room.ForeignKey
import org.threeten.bp.LocalDateTime

@Entity(
    tableName = "github_repositories_stargazers",
    primaryKeys = ["UserId", "RepositoryId"],
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

fun GithubStargazer.fromEntity(): com.ostanets.githubstars.domain.GithubStargazer {
    return com.ostanets.githubstars.domain.GithubStargazer(UserId, RepositoryId, StarredAt)
}

fun com.ostanets.githubstars.domain.GithubStargazer.toEntity(): GithubStargazer {
    return GithubStargazer(UserId, RepositoryId, StarredAt)
}

fun com.ostanets.githubstars.data.remote.github.GithubStargazer.toDomain(
    repositoryId: Long
): com.ostanets.githubstars.domain.GithubStargazer {
    return com.ostanets.githubstars.domain.GithubStargazer(UserId, repositoryId, StarredAt)
}