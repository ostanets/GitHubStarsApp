package com.ostanets.githubstars.domain

interface Artist {

    val name: String

    val icon: Image?

    val asdf: Asdf

    private data class ArtistImpl(override val name: String, override val icon: Image?) : Artist

    interface Asdf {

        val name: String

        val age: Int

    }

}
