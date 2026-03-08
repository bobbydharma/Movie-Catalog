package com.example.movie.moviedetail.domain.model

data class MovieDetailDomainModel(
    val title: String,
    val backdropPath: String,
    val genres: List<String>,
    val overview: String,
    val videos: List<VideoDomainModel>,
    val reviews: List<ReviewDomainModel>
)

data class VideoDomainModel(
    val id: String,
    val key: String,
    val name: String,
    val site: String
)

data class ReviewDomainModel(
    val id: String,
    val author: String,
    val avatarPath: String,
    val rating: Double,
    val date: String,
    val content: String
)