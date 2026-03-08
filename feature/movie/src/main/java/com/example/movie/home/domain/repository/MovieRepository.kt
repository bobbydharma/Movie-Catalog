package com.example.movie.home.domain.repository

import androidx.paging.PagingData
import com.example.movie.home.domain.model.MovieDomainModel
import kotlinx.coroutines.flow.Flow

interface MovieRepository {
    fun getMoviesByCategory(category: String): Flow<PagingData<MovieDomainModel>>
}