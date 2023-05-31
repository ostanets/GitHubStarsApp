package com.ostanets.githubstars.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.ostanets.githubstars.domain.GithubStargazer
import com.ostanets.githubstars.domain.Repo
import com.ostanets.githubstars.domain.Stargazer

@Entity(
    tableName = "github_repositories", foreignKeys = [ForeignKey(
        entity = UserBody::class,
        parentColumns = ["id"],
        childColumns = ["ownerId"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class RepoBody(
    @PrimaryKey override val id: Long,
    override val name: String,
    override val favourite: Boolean,
    override val ownerId: Long
) : Repo {
    override lateinit var stargazers: List<Stargazer>
}