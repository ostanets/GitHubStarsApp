package com.ostanets.githubstars.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.jakewharton.threetenabp.AndroidThreeTen
import com.ostanets.githubstars.data.GithubStarsAppDatabase
import com.ostanets.githubstars.data.GithubStarsAppRepositoryImpl
import com.ostanets.githubstars.data.GithubStarsDao
import com.ostanets.githubstars.databinding.ActivityMainBinding
import com.ostanets.githubstars.presenters.MainPresenter
import com.ostanets.githubstars.views.MainView
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter

class MainActivity : AppCompatActivity(), MainView {
    private lateinit var repository: GithubStarsAppRepositoryImpl
    private lateinit var dao: GithubStarsDao
    private lateinit var database: GithubStarsAppDatabase
    private lateinit var binding: ActivityMainBinding

    @InjectPresenter
    lateinit var mainPresenter: MainPresenter

    @ProvidePresenter
    fun provideMainPresenter(): MainPresenter {
        return MainPresenter(repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndroidThreeTen.init(this)
        database = GithubStarsAppDatabase.getDatabase(this)
        dao = database.getGithubStarsDao()
        repository = GithubStarsAppRepositoryImpl(dao)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mainPresenter = provideMainPresenter()

        setupSearch()
    }

    private fun setupSearch() {
        binding.btnSearch.setOnClickListener {
            val login = binding.etSearchOwner.text.toString()
            mainPresenter.getRepositories(login)
        }
    }

    override fun startSending() {
        binding.etSearchOwner.clearFocus()
        binding.etSearchOwner.isEnabled = false
        binding.btnSearch.isEnabled = false
    }

    override fun endSending() {
        binding.etSearchOwner.isEnabled = true
        binding.btnSearch.isEnabled = true
    }

    override fun showError(message: String) {
        Toast.makeText(this, "Error: $message", Toast.LENGTH_SHORT).show()
    }
}