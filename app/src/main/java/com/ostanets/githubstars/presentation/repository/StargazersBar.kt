package com.ostanets.githubstars.presentation.repository

import org.threeten.bp.LocalDate

data class StargazersBar(
    var Amount: Int,
    val Date: LocalDate,
    val Label: String,
    val GroupType: GroupType
)
