package com.example.movie.moviedetail.presentation.mapper

import com.example.movie.moviedetail.domain.model.MovieDetailDomainModel
import com.example.movie.moviedetail.presentation.model.MovieDetailUiModel
import com.example.movie.moviedetail.presentation.model.ReviewUiModel
import com.example.movie.moviedetail.presentation.model.VideoUiModel

fun MovieDetailDomainModel.toUiModel(): MovieDetailUiModel {
    return MovieDetailUiModel(
        title = this.title,
        backdropUrl = "https://image.tmdb.org/t/p/w1280${this.backdropPath}",
        genres = this.genres.joinToString(" | ").ifEmpty { "Unknown Genre" },
        overview = this.overview,

        videos = this.videos.filter { it.site == "YouTube" }.map { v ->
            VideoUiModel(id = v.id, key = v.key, name = v.name)
        },

        reviews = this.reviews.map { r ->
            val formattedAvatar =
                if (r.avatarPath.startsWith("/http")) r.avatarPath.removePrefix("/")
                else "https://image.tmdb.org/t/p/w200${r.avatarPath}"

            ReviewUiModel(
                id = r.id,
                author = r.author,
                avatarUrl = formattedAvatar,
                rating = r.rating,
                date = r.date.take(10),
                content = r.content
            )
        }
    )
}