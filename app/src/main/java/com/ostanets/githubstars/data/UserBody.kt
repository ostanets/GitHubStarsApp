package com.ostanets.githubstars.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ostanets.githubstars.domain.Repo
import com.ostanets.githubstars.domain.User

@Entity(tableName = "github_users")
data class UserBody(
    @PrimaryKey override val id: Long,
    override val login: String,
    override val avatarUrl: String
) : User {
    override lateinit var repos: List<Repo>
}