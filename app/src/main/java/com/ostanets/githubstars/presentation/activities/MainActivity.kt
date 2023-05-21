package com.ostanets.githubstars.presentation.activities

import android.os.Bundle
import android.widget.Toast
import com.jakewharton.threetenabp.AndroidThreeTen
import com.ostanets.githubstars.data.GithubStarsAppDatabase
import com.ostanets.githubstars.data.GithubStarsAppRepositoryImpl
import com.ostanets.githubstars.databinding.ActivityMainBinding
import com.ostanets.githubstars.presentation.presenters.MainPresenter
import com.ostanets.githubstars.presentation.views.MainView
import moxy.MvpAppCompatActivity
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter

class MainActivity : MvpAppCompatActivity(), MainView {
    private lateinit var binding: ActivityMainBinding

    @InjectPresenter
    lateinit var mainPresenter: MainPresenter

    @ProvidePresenter
    fun provideMainPresenter(): MainPresenter {
        val database = GithubStarsAppDatabase.getDatabase(this)
        val dao = database.getGithubStarsDao()
        val repository = GithubStarsAppRepositoryImpl(dao)
        return MainPresenter(repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndroidThreeTen.init(this)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupSearch()
    }

    private fun setupSearch() {
        binding.btnSearch.setOnClickListener {
            val login = binding.etSearchOwner.text.toString()
            mainPresenter.getRepositories(login)
        }
    }

    override fun startSearch() {
        binding.etSearchOwner.clearFocus()
        binding.etSearchOwner.isEnabled = false
        binding.btnSearch.isEnabled = false
    }

    override fun endSearch() {
        binding.etSearchOwner.isEnabled = true
        binding.btnSearch.isEnabled = true
    }

    override fun commitRepositories() {}

    override fun showError(message: String) {
        Toast.makeText(this, "Error: $message", Toast.LENGTH_SHORT).show()
    }
}