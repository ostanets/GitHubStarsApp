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