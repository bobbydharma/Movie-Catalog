package com.example.movie.home.data.mapper

import com.example.database.entity.MovieEntity
import com.example.movie.home.domain.model.MovieDomainModel
import com.example.network.model.MovieItem

fun MovieItem.toEntity(category: String): MovieEntity {
    return MovieEntity(
        id = this.id ?: 0,
        title = this.title ?: "",
        posterPath = this.posterPath ?: "",
        voteAverage = this.voteAverage ?: 0.0,
        voteCount = this.voteCount ?: 0,
        category = category,
    )
}

fun MovieEntity.toDomain(): MovieDomainModel = MovieDomainModel(
    id = this.id.toString(),
    title = this.title,
    posterUrl = "https://image.tmdb.org/t/p/w500$posterPath",
    rating = this.voteAverage,
    voteCount = this.voteCount
)