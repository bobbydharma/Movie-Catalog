package com.example.movie.home.domain.usecase

import androidx.paging.PagingData
import com.example.movie.home.domain.model.MovieDomainModel
import com.example.movie.home.domain.repository.MovieRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetMoviesUseCase @Inject constructor(
    private val repository: MovieRepository
) {
    fun execute(category: String): Flow<PagingData<MovieDomainModel>> {
        return repository.getMoviesByCategory(category)
    }
}