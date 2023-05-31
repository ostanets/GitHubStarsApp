package com.ostanets.githubstars.data

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import com.ostanets.githubstars.domain.Stargazer
import com.ostanets.githubstars.domain.User
import org.threeten.bp.LocalDateTime

@Entity(
    tableName = "github_repositories_stargazers",
    primaryKeys = ["UserId", "RepositoryId"],
    foreignKeys = [
        ForeignKey(
            entity = UserBody::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = RepoBody::class,
            parentColumns = ["id"],
            childColumns = ["repoId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class StargazerBody(
    val userId: Long,
    override val repoId: Long,
    override val starredAt: LocalDateTime,

    @Embedded private val userBody: UserBody
) : Stargazer {
    override val user: User
        get() = userBody
}