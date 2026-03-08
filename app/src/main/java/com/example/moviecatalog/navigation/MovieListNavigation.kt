package com.example.moviecatalog.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.movie.home.presentation.MovieListRoute
import com.example.movie.moviedetail.presentation.MovieDetailRoute

fun NavGraphBuilder.movieListNavigation(navController: NavController) {
    composable<MovieRoute.MovieList> {
        MovieListRoute(
            onMovieClick = { id ->
                navController.navigate(MovieRoute.MovieDetail(id))
            }
        )
    }

    composable<MovieRoute.MovieDetail> {
        MovieDetailRoute(
            onBackClick = {
                navController.popBackStack()
            }
        )
    }
}