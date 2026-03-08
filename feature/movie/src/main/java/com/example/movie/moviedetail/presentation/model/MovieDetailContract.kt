package com.example.movie.moviedetail.presentation.model

sealed interface MovieDetailState {
    data object Loading : MovieDetailState
    data class Success(val data: MovieDetailUiModel) : MovieDetailState
    data class Error(val message: String) : MovieDetailState
}

sealed interface MovieDetailEvent {
    data object FetchMovieDetail : MovieDetailEvent
    data class OnVideoError(val videoId: String) : MovieDetailEvent
}

sealed interface MovieDetailEffect {
    data class ShowVideoErrorRedirect(val videoId: String) : MovieDetailEffect
}