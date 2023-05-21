package com.ostanets.githubstars.presentation.viewholders

import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ostanets.githubstars.R
import com.ostanets.githubstars.databinding.ItemRepositoryBinding

class GithubRepositoryViewHolder(binding: ItemRepositoryBinding) : RecyclerView.ViewHolder(binding.root) {
    val view: View = binding.root

    val name: TextView = binding.twRepoName
    val likeButton: ImageButton = binding.btnLike

    fun setFavouriteStatus(status: Boolean) {
        if (status) {
            likeButton.setImageResource(R.drawable.baseline_remove_like)
        } else {
            likeButton.setImageResource(R.drawable.baseline_add_like)
        }
    }
}