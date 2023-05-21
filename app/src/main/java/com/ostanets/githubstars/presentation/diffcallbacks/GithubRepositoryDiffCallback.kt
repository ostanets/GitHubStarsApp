package com.ostanets.githubstars.presentation.diffcallbacks

import androidx.recyclerview.widget.DiffUtil
import com.ostanets.githubstars.domain.GithubRepository

class GithubRepositoryDiffCallback : DiffUtil.ItemCallback<GithubRepository>() {
    override fun areItemsTheSame(oldItem: GithubRepository, newItem: GithubRepository): Boolean {
        return oldItem.Id == newItem.Id
    }

    override fun areContentsTheSame(oldItem: GithubRepository, newItem: GithubRepository): Boolean {
        return oldItem == newItem
    }
}
