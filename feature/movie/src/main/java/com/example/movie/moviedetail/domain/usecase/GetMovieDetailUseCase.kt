package com.example.movie.moviedetail.domain.usecase

import com.example.movie.moviedetail.data.mapper.toDomain
import com.example.movie.moviedetail.domain.model.MovieDetailDomainModel
import com.example.movie.moviedetail.domain.repository.MovieDetailRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import javax.inject.Inject

class GetMovieDetailUseCase @Inject constructor(
    private val repository: MovieDetailRepository
) {
    suspend operator fun invoke(movieId: String): Result<MovieDetailDomainModel> {
        return try {
            val domainModel = coroutineScope {
                val detailDeferred = async { repository.getMovieDetail(movieId) }
                val videosDeferred = async { repository.getMovieVideos(movieId) }
                val reviewsDeferred = async { repository.getMovieReviews(movieId) }

                val detail = detailDeferred.await()
                val videos = videosDeferred.await().results ?: emptyList()
                val reviews = reviewsDeferred.await().results ?: emptyList()

                detail.toDomain(videos, reviews)
            }
            Result.success(domainModel)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}