package com.example.movie.moviedetail.domain.repository

import com.example.network.model.MovieDetailResponse
import com.example.network.model.ReviewResponse
import com.example.network.model.VideoResponse

interface MovieDetailRepository {
    suspend fun getMovieDetail(movieId: String): MovieDetailResponse
    suspend fun getMovieVideos(movieId: String): VideoResponse
    suspend fun getMovieReviews(movieId: String): ReviewResponse
}