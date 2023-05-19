package com.ostanets.githubstars.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "github_users")
data class GithubUser (
    @PrimaryKey
    val UserId: Long,
    val Login: String,
    val AvatarUrl: String
)

fun GithubUser.fromEntity(): com.ostanets.githubstars.domain.GithubUser {
    return com.ostanets.githubstars.domain.GithubUser(UserId, Login, AvatarUrl)
}

fun com.ostanets.githubstars.domain.GithubUser.toEntity(): GithubUser {
    return GithubUser(Id, Login, AvatarUrl)
}