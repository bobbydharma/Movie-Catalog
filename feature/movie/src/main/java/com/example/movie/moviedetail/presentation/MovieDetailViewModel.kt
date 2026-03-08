package com.example.movie.moviedetail.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.movie.moviedetail.domain.usecase.GetMovieDetailUseCase
import com.example.movie.moviedetail.presentation.mapper.toUiModel
import com.example.movie.moviedetail.presentation.model.MovieDetailEffect
import com.example.movie.moviedetail.presentation.model.MovieDetailEvent
import com.example.movie.moviedetail.presentation.model.MovieDetailState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MovieDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getMovieDetailUseCase: GetMovieDetailUseCase
) : ViewModel() {
    private val movieId: String = checkNotNull(savedStateHandle["movieId"])

    private val _uiState = MutableStateFlow<MovieDetailState>(MovieDetailState.Loading)
    val uiState: StateFlow<MovieDetailState> = _uiState.asStateFlow()

    private val _effect = MutableSharedFlow<MovieDetailEffect>()
    val effect: SharedFlow<MovieDetailEffect> = _effect.asSharedFlow()

    init {
        onEvent(MovieDetailEvent.FetchMovieDetail)
    }

    fun onEvent(event: MovieDetailEvent) {
        when (event) {
            is MovieDetailEvent.FetchMovieDetail -> fetchMovieDetail()
            is MovieDetailEvent.OnVideoError -> handleVideoError(event.videoId)
        }
    }

    private fun fetchMovieDetail() {
        viewModelScope.launch {
            _uiState.value = MovieDetailState.Loading
            val result = getMovieDetailUseCase(movieId)

            result.fold(
                onSuccess = { domainModel ->
                    val uiModel = domainModel.toUiModel()
                    _uiState.value = MovieDetailState.Success(uiModel)
                },
                onFailure = { exception ->
                    _uiState.value = MovieDetailState.Error(
                        message = exception.message ?: "Terjadi kesalahan"
                    )
                }
            )
        }
    }

    private fun handleVideoError(videoId: String) {
        viewModelScope.launch {
            _effect.emit(MovieDetailEffect.ShowVideoErrorRedirect(videoId))
        }
    }
}