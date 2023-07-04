package com.ostanets.githubstars.presentation.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.ostanets.githubstars.databinding.ItemRepositoryBinding
import com.ostanets.githubstars.domain.Repo

class RepositoriesListAdapter : ListAdapter<Repo, GithubRepositoryViewHolder>(
    GithubRepositoryDiffCallback()
) {
    var onRepositoryLongClickListener: ((Repo) -> Unit)? = null
    var onRepositoryClickListener: ((Repo) -> Unit)? = null
    var onLikeClickListener: ((Repo) -> Unit)? = null

    override fun getItemViewType(position: Int): Int {
        return ITEM_TYPE
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GithubRepositoryViewHolder {
        val binding = ItemRepositoryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return GithubRepositoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: GithubRepositoryViewHolder, position: Int) {
        val repository = getItem(position)

        holder.name.text = repository.name
        repository.favourite?.let { holder.setFavoriteStatus(it) }

        holder.view.setOnLongClickListener {
            onRepositoryLongClickListener?.invoke(repository)
            true
        }

        holder.view.setOnClickListener {
            onRepositoryClickListener?.invoke(repository)
        }

        holder.likeButton.setOnClickListener {
            onLikeClickListener?.invoke(repository)
        }
    }

    companion object {
        const val ITEM_TYPE = 1
        const val MAX_POOL_SIZE = 10
    }
}