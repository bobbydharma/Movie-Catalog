package com.example.movie.moviedetail.data.mapper

import com.example.movie.moviedetail.domain.model.MovieDetailDomainModel
import com.example.movie.moviedetail.domain.model.ReviewDomainModel
import com.example.movie.moviedetail.domain.model.VideoDomainModel
import com.example.network.model.MovieDetailResponse
import com.example.network.model.ReviewDetailResponse
import com.example.network.model.VideoDetailResponse

fun MovieDetailResponse.toDomain(
    videos: List<VideoDetailResponse>,
    reviews: List<ReviewDetailResponse>
): MovieDetailDomainModel {
    return MovieDetailDomainModel(
        title = this.title.orEmpty(),
        backdropPath = this.backdropPath.orEmpty(),
        genres = this.genres?.map { it.name.orEmpty() } ?: emptyList(),
        overview = this.overview ?: "No overview available.",

        videos = videos.map {
            VideoDomainModel(
                id = it.id ?: "",
                key = it.key ?: "",
                name = it.name ?: "Video",
                site = it.site ?: ""
            )
        },

        reviews = reviews.map {
            ReviewDomainModel(
                id = it.id ?: "",
                author = it.author ?: "Anonymous",
                avatarPath = it.authorDetails?.avatarPath.orEmpty(),
                rating = it.authorDetails?.rating ?: 0.0,
                date = it.createdAt ?: "",
                content = it.content ?: ""
            )
        }
    )
}