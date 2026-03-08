package com.example.movie.home.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.example.movie.home.domain.usecase.GetMoviesUseCase
import com.example.movie.home.presentation.model.MovieCategory
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MovieListViewModel @Inject constructor(
    getMoviesUseCase: GetMoviesUseCase
) : ViewModel() {
    val upcomingMovies =
        getMoviesUseCase.execute(MovieCategory.UPCOMING.title).cachedIn(viewModelScope)
    val nowPlayingMovies =
        getMoviesUseCase.execute(MovieCategory.NOW_PLAYING.title).cachedIn(viewModelScope)
    val topRatedMovies =
        getMoviesUseCase.execute(MovieCategory.TOP_RATED.title).cachedIn(viewModelScope)
    val popularMovies =
        getMoviesUseCase.execute(MovieCategory.POPULAR.title).cachedIn(viewModelScope)
}