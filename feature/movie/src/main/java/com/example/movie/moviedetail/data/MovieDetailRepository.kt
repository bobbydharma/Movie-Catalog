package com.example.movie.moviedetail.data

import com.example.movie.moviedetail.domain.repository.MovieDetailRepository
import com.example.network.api.MovieApiService
import com.example.network.model.MovieDetailResponse
import com.example.network.model.ReviewResponse
import com.example.network.model.VideoResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class MovieDetailRepositoryImpl @Inject constructor(
    private val api: MovieApiService
) : MovieDetailRepository {

    override suspend fun getMovieDetail(movieId: String): MovieDetailResponse {
        return withContext(Dispatchers.IO) {
            api.getMovieDetail(movieId)
        }
    }

    override suspend fun getMovieVideos(movieId: String): VideoResponse {
        return withContext(Dispatchers.IO) {
            api.getMovieVideos(movieId)
        }
    }

    override suspend fun getMovieReviews(movieId: String): ReviewResponse {
        return withContext(Dispatchers.IO) {
            api.getMovieReviews(movieId)
        }
    }
}