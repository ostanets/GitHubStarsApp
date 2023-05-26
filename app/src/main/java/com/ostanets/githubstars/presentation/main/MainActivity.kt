package com.ostanets.githubstars.presentation.main

import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.jakewharton.threetenabp.AndroidThreeTen
import com.ostanets.githubstars.data.GithubStarsAppDatabase
import com.ostanets.githubstars.data.GithubStarsAppRepositoryImpl
import com.ostanets.githubstars.databinding.ActivityMainBinding
import com.ostanets.githubstars.domain.GithubRepository
import com.ostanets.githubstars.presentation.main.RepositoriesListAdapter.Companion.DEFAULT_TYPE
import com.ostanets.githubstars.presentation.main.RepositoriesListAdapter.Companion.MAX_POOL_SIZE
import moxy.MvpAppCompatActivity
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter

class MainActivity : MvpAppCompatActivity(), MainView {
    private lateinit var binding: ActivityMainBinding
    private lateinit var repositoriesListAdapter: RepositoriesListAdapter

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

        setupRecyclerView()
        setupSearch()
    }

    private fun setupSearch() {
        binding.btnSearch.setOnClickListener {
            val login = binding.etSearchOwner.text.toString()
            mainPresenter.getRepositories(login)
        }
    }

    private fun setupRecyclerView() {
        val rvRepositoriesList = binding.rwRepositoriesList
        repositoriesListAdapter = RepositoriesListAdapter()

        with(rvRepositoriesList) {
            layoutManager = LinearLayoutManager(binding.root.context)
            adapter = repositoriesListAdapter
            recycledViewPool.setMaxRecycledViews(
                DEFAULT_TYPE,
                MAX_POOL_SIZE
            )
        }

        repositoriesListAdapter.onLikeClickListener = {
            mainPresenter.toggleLike(it)
        }
    }

    override fun setSearchState(state: String) {
        when (state) {
            MainView.START_SEARCH -> startSearch()
            MainView.END_SEARCH -> endSearch()
        }
    }

    private fun endSearch() {
        binding.etSearchOwner.isEnabled = true
        binding.btnSearch.isEnabled = true
    }

    private fun startSearch() {
        binding.etSearchOwner.clearFocus()
        binding.etSearchOwner.isEnabled = false
        binding.btnSearch.isEnabled = false
    }

    override fun commitRepositories(repositories: List<GithubRepository>) {
        repositoriesListAdapter.submitList(
            repositories.sortedWith(compareBy( { !it.Favourite }, { it.Name } ))
        )
    }

    override fun showError(message: String) {
        Toast.makeText(this, "Error: $message", Toast.LENGTH_SHORT).show()
    }
}