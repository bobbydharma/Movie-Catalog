package com.example.moviecatalog.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.movie.home.presentation.MovieListRoute

fun NavGraphBuilder.movieListNavigation(navController: NavController) {
    composable<MovieRoute.MovieList> {
        MovieListRoute(
            onMovieClick = { _ ->
            }
        )
    }
}