package com.ostanets.githubstars.presentation.repository

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.ostanets.githubstars.R
import com.ostanets.githubstars.data.GithubStarsAppDatabase
import com.ostanets.githubstars.data.GithubStarsAppRepositoryImpl
import com.ostanets.githubstars.databinding.ActivityRepositoryBinding
import moxy.MvpAppCompatActivity
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter

class RepositoryActivity : MvpAppCompatActivity(), RepositoryView {
    private lateinit var binding: ActivityRepositoryBinding
    private lateinit var barChart: BarChart
    private var repositoryId = UNKNOWN_REPOSITORY
    private var stargazersData = listOf<StargazersBar>()

    @InjectPresenter
    lateinit var repositoryPresenter: RepositoryPresenter

    @ProvidePresenter
    fun provideRepositoryPresenter(): RepositoryPresenter {
        val database = GithubStarsAppDatabase.getDatabase(this)
        val dao = database.getGithubStarsDao()
        val repository = GithubStarsAppRepositoryImpl(dao)
        return RepositoryPresenter(repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        parseIntent()
        binding = ActivityRepositoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupBackButton()
        setupNextButton()
        setupBarChart()

        repositoryPresenter.getRepository(repositoryId)
    }

    private fun setupNextButton() {
        binding.arrRight.setOnClickListener {
            repositoryPresenter.nextChartPage()
        }
    }

    private fun setupBackButton() {
        binding.btnBack.setOnClickListener {
            this.finish()
        }
    }

    private fun setupBarChart() {
        barChart = binding.histogram

        barChart.description.isEnabled = false

        barChart.setOnChartValueSelectedListener(object : OnChartValueSelectedListener {
            override fun onValueSelected(e: Entry?, h: Highlight?) {
                val selectedEntryIndex = e?.x?.toInt()
                val selectedStargazer = selectedEntryIndex?.let { stargazersData.getOrNull(it) }

                selectedStargazer?.let { stargazer ->
                    val date = stargazer.Date
                    Toast.makeText(applicationContext, date.toString(), Toast.LENGTH_SHORT).show()
                }
            }

            override fun onNothingSelected() {}
        })
    }

    private fun parseIntent() {
        if (!intent.hasExtra(EXTRA_REPOSITORY)) {
            throw RuntimeException("Param repository is absent")
        }
        repositoryId = intent.getLongExtra(EXTRA_REPOSITORY, UNKNOWN_REPOSITORY)
        if (repositoryId < 0) {
            throw RuntimeException("Bad repository id $repositoryId")
        }
    }

    override fun setOwner(name: String) {
        binding.tvRepoOwner.text = name
    }

    override fun setRepository(name: String) {
        binding.tvRepoName.text = name
    }

    override fun setStarsCount(count: Int) {
        binding.tvRepoStarsCount.text = count.toString()
    }

    override fun setFavorite(status: Boolean) {
        val likeButton = binding.btnFavourite
        if (status) {
            likeButton.setImageResource(R.drawable.baseline_remove_like)
            likeButton.contentDescription = getString(R.string.remove_from_liked)
        } else {
            likeButton.setImageResource(R.drawable.baseline_add_like)
            likeButton.contentDescription = getString(R.string.add_to_liked)
        }
    }

    override fun commitStargazers(stars: List<StargazersBar>) {
        stargazersData = stars

        val entries = stargazersData.mapIndexed { index, stargazer ->
            BarEntry(index.toFloat(), stargazer.Amount.toFloat(), stargazer.Label)
        }

        val dataSet = BarDataSet(entries, "Stargazers")
        dataSet.color = Color.BLUE
        dataSet.setDrawValues(false)

        val data = BarData(dataSet)

        barChart.xAxis.labelCount = stargazersData.size

        barChart.data = data

        barChart.xAxis.valueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                val index = value.toInt()
                return if (index >= 0 && index < stargazersData.size) {
                    stargazersData[index].Label
                } else {
                    ""
                }
            }
        }

        barChart.invalidate()
    }

    override fun hideProgressBar() {
        binding.progressBackground.visibility = View.GONE
        binding.progressBar.visibility = View.GONE
    }

    override fun showError(message: String) {
        Toast.makeText(this, "Error: $message", Toast.LENGTH_SHORT).show()
    }

    companion object {
        private const val EXTRA_REPOSITORY = "extra_repository_id"
        private const val UNKNOWN_REPOSITORY = -1L

        fun newIntentShowRepository(repositoryId: Long, context: Context): Intent {
            val intent = Intent(context, RepositoryActivity::class.java)
            intent.putExtra(EXTRA_REPOSITORY, repositoryId)
            return intent
        }
    }
}