package com.example.network.api

import com.example.network.model.MovieResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface MovieApiService {
    @GET("movie/now_playing")
    suspend fun getMovieNowPlaying(
        @Query("page") page: Int,
        @Query("language") language: String = "en-US"
    ): MovieResponse

    @GET("movie/top_rated")
    suspend fun getMovieTopRated(
        @Query("page") page: Int,
        @Query("language") language: String = "en-US"
    ): MovieResponse

    @GET("movie/popular")
    suspend fun getMoviePopular(
        @Query("page") page: Int,
        @Query("language") language: String = "en-US"
    ): MovieResponse

    @GET("movie/upcoming")
    suspend fun getMovieUpcoming(
        @Query("page") page: Int,
        @Query("language") language: String = "en-US"
    ): MovieResponse
}