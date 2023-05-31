package com.ostanets.githubstars.data.remote.github

import com.ostanets.githubstars.domain.Artist
import com.ostanets.githubstars.domain.Image
import com.ostanets.githubstars.domain.sdf

data class ArtistBody(
    override val name: String,
    ) : Artist, Artist.Asdf {

    override lateinit var icon: Image




    }