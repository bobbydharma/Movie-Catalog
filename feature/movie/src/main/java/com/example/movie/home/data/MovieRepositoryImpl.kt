package com.example.movie.home.data

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.example.database.MovieDatabase
import com.example.movie.home.data.mapper.toDomain
import com.example.movie.home.domain.model.MovieDomainModel
import com.example.movie.home.domain.repository.MovieRepository
import com.example.network.api.MovieApiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class MovieRepositoryImpl @Inject constructor(
    private val apiService: MovieApiService,
    private val database: MovieDatabase
) : MovieRepository {

    @OptIn(ExperimentalPagingApi::class)
    override fun getMoviesByCategory(category: String): Flow<PagingData<MovieDomainModel>> {
        val pagingSourceFactory = { database.movieDao().getMoviesByCategory(category) }

        return Pager(
            config = PagingConfig(pageSize = 20, prefetchDistance = 5, enablePlaceholders = true),
            remoteMediator = MovieRemoteMediator(apiService, database, category),
            pagingSourceFactory = pagingSourceFactory
        ).flow.map { pagingData ->
            pagingData.map { movieEntity -> movieEntity.toDomain() }
        }
    }
}