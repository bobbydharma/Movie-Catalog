package com.example.movie.home.data

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.paging.RemoteMediator.MediatorResult
import androidx.room.withTransaction
import coil.network.HttpException
import com.example.database.MovieDatabase
import com.example.database.entity.MovieEntity
import com.example.database.entity.RemoteKeyEntity
import com.example.movie.home.data.mapper.toEntity
import com.example.movie.home.presentation.model.MovieCategory
import com.example.network.api.MovieApiService
import java.io.IOException

@OptIn(ExperimentalPagingApi::class)
class MovieRemoteMediator(
    private val api: MovieApiService,
    private val db: MovieDatabase,
    private val category: String
) : RemoteMediator<Int, MovieEntity>() {

    override suspend fun load(loadType: LoadType, state: PagingState<Int, MovieEntity>): MediatorResult {
        val page = when (loadType) {
            LoadType.REFRESH -> 1
            LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
            LoadType.APPEND -> {
                val remoteKey = db.remoteKeyDao().getRemoteKey(category)
                val nextKey = remoteKey?.nextKey
                    ?: return MediatorResult.Success(endOfPaginationReached = remoteKey != null)
                nextKey
            }
        }

        return try {
            val response = when (category) {
                MovieCategory.NOW_PLAYING.title -> api.getMovieNowPlaying(page = page)
                MovieCategory.UPCOMING.title -> api.getMovieUpcoming(page = page)
                MovieCategory.TOP_RATED.title -> api.getMovieTopRated(page = page)
                MovieCategory.POPULAR.title -> api.getMoviePopular(page = page)
                else -> api.getMovieNowPlaying(page = page)
            }
            val rawMovies = response.results ?: emptyList()
            val totalPages = response.totalPages ?: 1
            val endOfPaginationReached = page >= totalPages || rawMovies.isEmpty()
            val currentTime = System.currentTimeMillis()

            val validMovieEntities = rawMovies.mapIndexed { index, response ->
                val entity = response.toEntity(category)
                entity.copy(addedAt = currentTime + index)
            }

            db.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    db.remoteKeyDao().clearRemoteKeys(category)
                    db.movieDao().clearMoviesByCategory(category)
                }

                db.remoteKeyDao().insertKey(
                    RemoteKeyEntity(
                        id = category,
                        prevKey = if (page == 1) null else page - 1,
                        nextKey = if (endOfPaginationReached) null else page + 1
                    )
                )

                db.movieDao().insertAll(validMovieEntities)
            }

            MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)

        } catch (exception: IOException) {
            MediatorResult.Error(exception)
        } catch (exception: HttpException) {
            MediatorResult.Error(exception)
        }
    }
}