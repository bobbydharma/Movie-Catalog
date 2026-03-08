package com.example.network.api

import com.example.network.model.MovieDetailResponse
import com.example.network.model.MovieResponse
import com.example.network.model.ReviewResponse
import com.example.network.model.VideoResponse
import retrofit2.http.GET
import retrofit2.http.Path
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

    @GET("movie/{movie_id}")
    suspend fun getMovieDetail(
        @Path("movie_id") movieId: String,
    ): MovieDetailResponse

    @GET("movie/{movie_id}/videos")
    suspend fun getMovieVideos(
        @Path("movie_id") movieId: String,
    ): VideoResponse

    @GET("movie/{movie_id}/reviews")
    suspend fun getMovieReviews(
        @Path("movie_id") movieId: String,
    ): ReviewResponse
}