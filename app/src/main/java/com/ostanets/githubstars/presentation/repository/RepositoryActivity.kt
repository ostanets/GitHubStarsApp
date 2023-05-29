package com.ostanets.githubstars.presentation.repository

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.ostanets.githubstars.R
import moxy.MvpAppCompatActivity

class RepositoryActivity : MvpAppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_repository)
    }

    companion object {
        private const val EXTRA_REPOSITORY = "extra_id"
        private const val UNKNOWN_REPOSITORY = -1L

        fun newIntentShowRepository(repositoryId: Long, context: Context): Intent {
            val intent = Intent(context, RepositoryActivity::class.java)
            intent.putExtra(EXTRA_REPOSITORY, repositoryId)
            return intent
        }
    }
}