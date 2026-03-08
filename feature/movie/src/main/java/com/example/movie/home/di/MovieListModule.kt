package com.example.movie.home.di

import com.example.database.MovieDatabase
import com.example.movie.home.data.MovieRepositoryImpl
import com.example.movie.home.domain.repository.MovieRepository
import com.example.network.api.MovieApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object MovieListModule {
    @Provides
    @Singleton
    fun provideMovieListRepository(api: MovieApiService, db: MovieDatabase): MovieRepository {
        return MovieRepositoryImpl(api, db)
    }
}