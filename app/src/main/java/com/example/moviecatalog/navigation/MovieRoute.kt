package com.example.moviecatalog.navigation

import kotlinx.serialization.Serializable

object MovieRoute{
    @Serializable
    data object MovieList

    @Serializable
    data class MovieDetail(val movieId: String)
}