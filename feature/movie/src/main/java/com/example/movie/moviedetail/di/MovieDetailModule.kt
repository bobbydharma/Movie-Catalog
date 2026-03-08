package com.example.movie.moviedetail.di

import com.example.movie.moviedetail.data.MovieDetailRepositoryImpl
import com.example.movie.moviedetail.domain.repository.MovieDetailRepository
import com.example.network.api.MovieApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object MovieDetailModule {
    @Provides
    @Singleton
    fun provideMovieDetailRepository(api: MovieApiService): MovieDetailRepository {
        return MovieDetailRepositoryImpl(api)
    }
}