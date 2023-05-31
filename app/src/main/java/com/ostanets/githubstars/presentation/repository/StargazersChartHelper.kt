package com.ostanets.githubstars.presentation.repository

import com.ostanets.githubstars.domain.GithubStargazer
import org.threeten.bp.LocalDate
import org.threeten.bp.temporal.ChronoUnit

class StargazersChartHelper {
    companion object Factory {
        fun getBars(stargazers: List<GithubStargazer>, groupType: GroupType): List<StargazersBar> {
            if (stargazers.isEmpty()) return emptyList()
            val sortedStargazers = stargazers.sortedBy { it.StarredAt }
            return when (groupType) {
                GroupType.DAILY -> getDailyBars(sortedStargazers)
                GroupType.MONTHLY -> getMonthlyBars(sortedStargazers)
                GroupType.YEARLY -> getYearlyBars(sortedStargazers)
            }
        }

        fun getStargazers(
            bar: StargazersBar,
            stargazers: List<GithubStargazer>,
        ): List<GithubStargazer> {
            return when (bar.GroupType) {
                GroupType.DAILY -> getStargazersOfDay(bar.Date, stargazers)
                GroupType.MONTHLY -> getStargazersOfMonth(bar.Date, stargazers)
                GroupType.YEARLY -> getStargazersOfYear(bar.Date, stargazers)
            }
        }

        private fun getDailyBars(sortedStargazers: List<GithubStargazer>): List<StargazersBar> {
            val result = arrayListOf<StargazersBar>()
            val firstStarDate = sortedStargazers.first().StarredAt
            val lastStarDate = sortedStargazers.last().StarredAt
            val offset = if (firstStarDate.toLocalDate().isEqual(lastStarDate.toLocalDate())) {
                1
            } else {
                2
            }
            val daysCount = ChronoUnit.DAYS.between(firstStarDate, lastStarDate) + offset
            val barsStartDate = firstStarDate.toLocalDate()

            for (i in 0 until daysCount) {
                val date = barsStartDate.plusDays(i)
                result.add(StargazersBar(0, date, date.toString(), GroupType.DAILY))
            }

            for (stargazer in sortedStargazers) {
                val starredDate = stargazer.StarredAt.toLocalDate()
                val bar = result.find {
                    it.Date.isEqual(starredDate)
                }
                bar!!.Amount++
            }

            return result
        }

        private fun getMonthlyBars(sortedStargazers: List<GithubStargazer>): List<StargazersBar> {
            val result = arrayListOf<StargazersBar>()
            val firstStarDate = sortedStargazers.first().StarredAt
            val lastStarDate = sortedStargazers.last().StarredAt
            val offset = if (firstStarDate.toLocalDate().withDayOfMonth(1)
                    .isEqual(lastStarDate.toLocalDate().withDayOfMonth(1))) {
                1
            } else {
                2
            }
            val monthsCount = ChronoUnit.MONTHS.between(firstStarDate, lastStarDate) + offset
            val barsStartDate = firstStarDate.toLocalDate().withDayOfMonth(1)

            for (i in 0 until monthsCount) {
                val date = barsStartDate.plusMonths(i)
                result.add(
                    StargazersBar(
                        0,
                        date,
                        "${date.month.name} ${date.year}",
                        GroupType.MONTHLY
                    )
                )
            }

            for (stargazer in sortedStargazers) {
                val starredDate = stargazer.StarredAt.toLocalDate().withDayOfMonth(1)
                val bar = result.find {
                    it.Date.isEqual(starredDate)
                }
                bar!!.Amount++
            }

            return result
        }

        private fun getYearlyBars(sortedStargazers: List<GithubStargazer>): List<StargazersBar> {
            val result = arrayListOf<StargazersBar>()
            val firstStarDate = sortedStargazers.first().StarredAt
            val lastStarDate = sortedStargazers.last().StarredAt
            val offset = if (firstStarDate.toLocalDate().withDayOfYear(1)
                    .isEqual(lastStarDate.toLocalDate().withDayOfYear(1))) {
                1
            } else {
                2
            }
            val yearsCount = ChronoUnit.YEARS.between(firstStarDate, lastStarDate) + offset
            val barsStartDate = firstStarDate.toLocalDate().withDayOfYear(1)

            for (i in 0 until yearsCount) {
                val date = barsStartDate.plusMonths(i)
                result.add(StargazersBar(0, date, "${date.year}", GroupType.YEARLY))
            }

            for (stargazer in sortedStargazers) {
                val starredDate = stargazer.StarredAt.toLocalDate().withDayOfYear(1)
                val bar = result.find {
                    it.Date.isEqual(starredDate)
                }
                bar!!.Amount++
            }

            return result
        }

        private fun getStargazersOfDay(
            barDate: LocalDate,
            stargazers: List<GithubStargazer>,
        ): List<GithubStargazer> {

            val result = arrayListOf<GithubStargazer>()

            for (stargazer in stargazers) {
                val stargazerDate = stargazer.StarredAt.toLocalDate()
                if (stargazerDate.isEqual(barDate)) {
                    result.add(stargazer)
                }
            }
            return result
        }

        private fun getStargazersOfMonth(
            barDate: LocalDate,
            stargazers: List<GithubStargazer>,
        ): List<GithubStargazer> {

            val result = arrayListOf<GithubStargazer>()

            for (stargazer in stargazers) {
                val stargazerDate = stargazer.StarredAt.toLocalDate().withDayOfMonth(1)
                if (stargazerDate.isEqual(barDate)) {
                    result.add(stargazer)
                }
            }
            return result
        }

        private fun getStargazersOfYear(
            barDate: LocalDate,
            stargazers: List<GithubStargazer>,
        ): List<GithubStargazer> {

            val result = arrayListOf<GithubStargazer>()

            for (stargazer in stargazers) {
                val stargazerDate = stargazer.StarredAt.toLocalDate().withDayOfYear(1)
                if (stargazerDate.isEqual(barDate)) {
                    result.add(stargazer)
                }
            }
            return result
        }
    }
}