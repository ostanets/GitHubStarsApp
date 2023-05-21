package com.ostanets.githubstars.presentation.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.ostanets.githubstars.databinding.ItemRepositoryBinding
import com.ostanets.githubstars.domain.GithubRepository
import com.ostanets.githubstars.presentation.diffcallbacks.GithubRepositoryDiffCallback
import com.ostanets.githubstars.presentation.viewholders.GithubRepositoryViewHolder

class RepositoriesListAdapter : ListAdapter<GithubRepository, GithubRepositoryViewHolder>(
    GithubRepositoryDiffCallback()
) {
    var onRepositoryLongClickListener: ((GithubRepository) -> Unit)? = null
    var onRepositoryClickListener: ((GithubRepository) -> Unit)? = null
    var onLikeClickListener: ((GithubRepository) -> Unit)? = null

    override fun getItemViewType(position: Int): Int {
        return DEFAULT_TYPE
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

        holder.name.text = repository.Name
        holder.setFavouriteStatus(repository.Favourite)

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
        const val DEFAULT_TYPE = 1
        const val MAX_POOL_SIZE = 10
    }
}