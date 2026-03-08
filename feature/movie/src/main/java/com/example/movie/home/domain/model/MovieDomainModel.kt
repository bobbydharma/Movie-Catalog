package com.example.movie.home.domain.model

data class MovieDomainModel(
    val id: String,
    val title: String,
    val posterUrl: String,
    val rating: Double,
    val voteCount: Int
)