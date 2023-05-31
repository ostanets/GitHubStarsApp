package com.ostanets.githubstars.presentation.main

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jakewharton.threetenabp.AndroidThreeTen
import com.ostanets.githubstars.data.AppDatabase
import com.ostanets.githubstars.data.AppRepoImpl
import com.ostanets.githubstars.databinding.ActivityMainBinding
import com.ostanets.githubstars.domain.GithubRepository
import com.ostanets.githubstars.presentation.main.RepositoriesListAdapter.Companion.ITEM_TYPE
import com.ostanets.githubstars.presentation.main.RepositoriesListAdapter.Companion.MAX_POOL_SIZE
import com.ostanets.githubstars.presentation.repository.RepositoryActivity
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
        val database = AppDatabase.getDatabase(this)
        val dao = database.getGithubStarsDao()
        val repository = AppRepoImpl(dao)
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

        setupOnRVScrollListener()

        with(rvRepositoriesList) {
            layoutManager = LinearLayoutManager(binding.root.context)
            adapter = repositoriesListAdapter
            recycledViewPool.setMaxRecycledViews(
                ITEM_TYPE,
                MAX_POOL_SIZE
            )
        }

        repositoriesListAdapter.onRepositoryClickListener = {
            val intent = RepositoryActivity.newIntentShowRepository(it.Id, this)
            startActivity(intent)
        }

        repositoriesListAdapter.onLikeClickListener = {
            mainPresenter.toggleLike(it)
        }
    }

    private fun setupOnRVScrollListener() {
        val rvRepositoriesList = binding.rwRepositoriesList
        val scrollListener = object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (!recyclerView.canScrollVertically(1)) {
                    mainPresenter.loadMoreRepositories()
                }
            }
        }
        rvRepositoriesList.addOnScrollListener(scrollListener)
    }

    override fun setSearchState(state: String) {
        when (state) {
            MainView.START_SEARCH -> startSearch()
            MainView.END_SEARCH -> endSearch()
            MainView.CACHE_LOADED -> cacheLoaded()
            MainView.LOAD_MORE_REPOSITORIES -> loadMoreRepositories()
        }
    }

    private fun endSearch() {
        binding.etSearchOwner.isEnabled = true
        binding.btnSearch.isEnabled = true
        binding.progressBackground.visibility = View.GONE
        binding.progressBar.visibility = View.GONE
        binding.rwRepositoriesList.visibility = View.VISIBLE
    }

    private fun startSearch() {
        binding.etSearchOwner.clearFocus()
        binding.etSearchOwner.isEnabled = false
        binding.btnSearch.isEnabled = false
        binding.rwRepositoriesList.visibility = View.GONE
        binding.progressBackground.visibility = View.VISIBLE
        binding.progressBar.visibility = View.VISIBLE
    }

    private fun cacheLoaded() {
        binding.etSearchOwner.clearFocus()
        binding.etSearchOwner.isEnabled = false
        binding.btnSearch.isEnabled = false
        binding.progressBackground.visibility = View.GONE
        binding.progressBar.visibility = View.GONE
        binding.rwRepositoriesList.visibility = View.VISIBLE
    }

    private fun loadMoreRepositories() {
        binding.etSearchOwner.clearFocus()
        binding.etSearchOwner.isEnabled = false
        binding.btnSearch.isEnabled = false
        binding.progressBackground.visibility = View.VISIBLE
        binding.progressBar.visibility = View.VISIBLE
    }

    override fun commitRepositories(repositories: List<GithubRepository>) {
        repositoriesListAdapter.submitList(
            repositories.sortedWith(compareBy ({ !it.Favorite }, { it.Name }))
        )
    }

    override fun showError(message: String) {
        Toast.makeText(this, "Error: $message", Toast.LENGTH_SHORT).show()
    }
}