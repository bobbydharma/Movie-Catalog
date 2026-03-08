package com.example.movie.home.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SecondaryScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.pullToRefresh
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import com.example.movie.home.domain.model.MovieDomainModel
import com.example.movie.home.presentation.component.MovieCardItem
import com.example.movie.home.presentation.model.MovieCategory
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlin.collections.get

@Composable
fun MovieListRoute(
    viewModel: MovieListViewModel = hiltViewModel(),
    onMovieClick: (String) -> Unit
) {
    val nowPlayingMovies = viewModel.nowPlayingMovies.collectAsLazyPagingItems()
    val upcomingMovies = viewModel.upcomingMovies.collectAsLazyPagingItems()
    val topRatedMovies = viewModel.topRatedMovies.collectAsLazyPagingItems()
    val popularMovies = viewModel.popularMovies.collectAsLazyPagingItems()

    MovieListScreen(
        nowPlayingMovies = nowPlayingMovies,
        upcomingMovies = upcomingMovies,
        topRatedMovies = topRatedMovies,
        popularMovies = popularMovies,
        onMovieClick = onMovieClick
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovieListScreen(
    nowPlayingMovies: LazyPagingItems<MovieDomainModel>,
    upcomingMovies: LazyPagingItems<MovieDomainModel>,
    topRatedMovies: LazyPagingItems<MovieDomainModel>,
    popularMovies: LazyPagingItems<MovieDomainModel>,
    onMovieClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val categories = MovieCategory.entries
    val pagerState = rememberPagerState(pageCount = { categories.size })
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        modifier = modifier,
        topBar = {
            Column {
                TopAppBar(
                    title = { Text("Movies", fontWeight = FontWeight.Bold) },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color(0xFF0B101A),
                        titleContentColor = Color.White
                    )
                )

                SecondaryScrollableTabRow(
                    selectedTabIndex = pagerState.currentPage,
                    containerColor = Color(0xFF0B101A),
                    contentColor = Color.White,
                    edgePadding = 16.dp,
                    indicator = {
                        TabRowDefaults.SecondaryIndicator(
                            modifier = Modifier.tabIndicatorOffset(pagerState.currentPage),
                            color = Color(0xFF00E5FF)
                        )
                    },
                    divider = {
                        HorizontalDivider(color = Color.DarkGray.copy(alpha = 0.5f))
                    }
                ) {
                    categories.forEachIndexed { index, category ->
                        val isSelected = pagerState.currentPage == index
                        Tab(
                            selected = isSelected,
                            onClick = {
                                coroutineScope.launch {
                                    pagerState.animateScrollToPage(index)
                                }
                            },
                            text = {
                                Text(
                                    text = category.title,
                                    color = if (isSelected) Color(0xFF00E5FF) else Color.Gray,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        )
                    }
                }
            }
        },
        containerColor = Color(0xFF0B101A)
    ) { innerPadding ->

        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) { page ->
            val pagingItems = when (categories[page]) {
                MovieCategory.NOW_PLAYING -> nowPlayingMovies
                MovieCategory.UPCOMING -> upcomingMovies
                MovieCategory.TOP_RATED -> topRatedMovies
                MovieCategory.POPULAR -> popularMovies
            }

            MovieListContent(
                moviesPagingItems = pagingItems,
                onMovieClick = onMovieClick
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovieListContent(
    moviesPagingItems: LazyPagingItems<MovieDomainModel>,
    onMovieClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var isManualRefreshing by remember { mutableStateOf(false) }
    val isInitialLoading = moviesPagingItems.loadState.refresh is LoadState.Loading && moviesPagingItems.itemCount == 0
    val pullToRefreshState = rememberPullToRefreshState()
    val gridState = rememberLazyGridState()
    val isAtTop = !gridState.canScrollBackward

    LaunchedEffect(moviesPagingItems.loadState.refresh) {
        if (moviesPagingItems.loadState.refresh !is LoadState.Loading) {
            isManualRefreshing = false
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .pullToRefresh(
                isRefreshing = isManualRefreshing,
                state = pullToRefreshState,
                onRefresh = {
                    isManualRefreshing = true
                    moviesPagingItems.refresh()
                },
                enabled = isAtTop
            )
    ) {
        LazyVerticalGrid(
            state = gridState,
            columns = GridCells.Adaptive(minSize = 160.dp),
            contentPadding = PaddingValues(16.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(
                count = moviesPagingItems.itemCount,
                key = moviesPagingItems.itemKey { it.id },
                contentType = moviesPagingItems.itemContentType { "Movie" }
            ) { index ->
                val movie = moviesPagingItems[index]
                if (movie != null) {
                    MovieCardItem(
                        movie = movie,
                        onClick = onMovieClick
                    )
                }
            }

            moviesPagingItems.apply {
                when {
                    isInitialLoading -> {
                        item(span = { GridItemSpan(maxLineSpan) }) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(top = 100.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(color = Color(0xFF00E5FF))
                            }
                        }
                    }

                    loadState.append is LoadState.Loading -> {
                        item(span = { GridItemSpan(maxLineSpan) }) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(color = Color(0xFF00E5FF))
                            }
                        }
                    }

                    loadState.refresh is LoadState.Error -> {
                        if (moviesPagingItems.itemCount == 0) {
                            item(span = { GridItemSpan(maxLineSpan) }) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(32.dp)
                                        .padding(top = 100.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Text(
                                        text = "Gagal memuat data. Periksa koneksi Anda.",
                                        color = Color.White
                                    )
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Button(
                                        onClick = { retry() },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = Color(0xFF00E5FF),
                                            contentColor = Color.Black
                                        )
                                    ) {
                                        Text("Coba Lagi")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        PullToRefreshDefaults.Indicator(
            state = pullToRefreshState,
            isRefreshing = isManualRefreshing,
            containerColor = Color(0xFF1E293B),
            color = Color(0xFF00E5FF),
            modifier = Modifier.align(Alignment.TopCenter)
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun MovieListContentPreview() {
    val dummyMovies = listOf(
        MovieDomainModel(id = "1", title = "Mercy", posterUrl = "", rating = 9.0, voteCount = 76),
        MovieDomainModel(
            id = "2",
            title = "Demon Slayer",
            posterUrl = "",
            rating = 9.0,
            voteCount = 76
        ),
        MovieDomainModel(
            id = "3",
            title = "Inception",
            posterUrl = "",
            rating = 9.0,
            voteCount = 76
        ),
        MovieDomainModel(
            id = "4",
            title = "Interstellar",
            posterUrl = "",
            rating = 9.0,
            voteCount = 76
        ),
        MovieDomainModel(
            id = "5",
            title = "The Matrix",
            posterUrl = "",
            rating = 9.0,
            voteCount = 76
        )
    )

    val fakePagingFlow = flowOf(PagingData.from(dummyMovies))
    val dummyPagingItems = fakePagingFlow.collectAsLazyPagingItems()

    MovieListContent(
        moviesPagingItems = dummyPagingItems,
        onMovieClick = { /* Do nothing di preview */ }
    )
}