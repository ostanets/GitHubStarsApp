package com.ostanets.githubstars.data

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import org.threeten.bp.LocalDateTime
import org.threeten.bp.format.DateTimeFormatter

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
    val StargazerUserId: Long,
    val RepositoryId: Long,
    val StarredAt: LocalDateTime,

    @Embedded val User: GithubUser,
)

fun GithubStargazer.fromEntity(): com.ostanets.githubstars.domain.GithubStargazer {
    return com.ostanets.githubstars.domain.GithubStargazer(
        User.fromEntity(),
        RepositoryId,
        StarredAt
    )
}

fun com.ostanets.githubstars.domain.GithubStargazer.toEntity(): GithubStargazer {
    return GithubStargazer(User.Id, RepositoryId, StarredAt, User.toEntity())
}

fun com.ostanets.githubstars.data.remote.github.GithubStargazer.toDomain(
    repositoryId: Long,
): com.ostanets.githubstars.domain.GithubStargazer {
    return com.ostanets.githubstars.domain.GithubStargazer(
        com.ostanets.githubstars.domain.GithubUser(User.Id, User.Login, User.AvatarUrl),
        repositoryId,
        LocalDateTime.parse(StarredAt, DateTimeFormatter.ISO_DATE_TIME)
    )
}