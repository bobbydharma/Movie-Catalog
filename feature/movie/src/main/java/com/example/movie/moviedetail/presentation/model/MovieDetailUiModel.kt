package com.example.movie.moviedetail.presentation.model

data class MovieDetailUiModel(
    val title: String,
    val backdropUrl: String,
    val genres: String,
    val overview: String,
    val videos: List<VideoUiModel>,
    val reviews: List<ReviewUiModel>
)

data class VideoUiModel(
    val id: String,
    val key: String,
    val name: String
) {
    val thumbnailUrl: String
        get() = "https://img.youtube.com/vi/$key/hqdefault.jpg"
}

data class ReviewUiModel(
    val id: String,
    val author: String,
    val avatarUrl: String?,
    val rating: Double,
    val date: String,
    val content: String
)