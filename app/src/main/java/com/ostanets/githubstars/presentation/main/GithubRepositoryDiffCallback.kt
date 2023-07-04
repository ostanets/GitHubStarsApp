package com.ostanets.githubstars.presentation.main

import androidx.recyclerview.widget.DiffUtil
import com.ostanets.githubstars.data.RepoBody
import com.ostanets.githubstars.domain.Repo

class GithubRepositoryDiffCallback : DiffUtil.ItemCallback<Repo>() {
    override fun areItemsTheSame(oldItem: Repo, newItem: Repo): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Repo, newItem: Repo): Boolean {
        return oldItem as RepoBody == newItem as RepoBody
    }
}
