package com.example.movie.moviedetail.presentation

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.movie.moviedetail.presentation.component.HeaderSection
import com.example.movie.moviedetail.presentation.component.HeroSection
import com.example.movie.moviedetail.presentation.component.MovieInfoSection
import com.example.movie.moviedetail.presentation.component.ReviewBottomSheetContent
import com.example.movie.moviedetail.presentation.component.ReviewItem
import com.example.movie.moviedetail.presentation.component.StickyAppBar
import com.example.movie.moviedetail.presentation.component.VideoListSection
import com.example.movie.moviedetail.presentation.component.YoutubePlayerComponent
import com.example.movie.moviedetail.presentation.model.MovieDetailEffect
import com.example.movie.moviedetail.presentation.model.MovieDetailEvent
import com.example.movie.moviedetail.presentation.model.MovieDetailState
import com.example.movie.moviedetail.presentation.model.MovieDetailUiModel
import com.example.movie.moviedetail.presentation.model.ReviewUiModel
import com.example.movie.moviedetail.presentation.model.VideoUiModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@Composable
fun MovieDetailRoute(
    viewModel: MovieDetailViewModel = hiltViewModel(),
    onBackClick: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    var playingVideoId by remember { mutableStateOf<String?>(null) }
    var showReviewSheet by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                is MovieDetailEffect.ShowVideoErrorRedirect -> {
                    val snackbarJob = launch {
                        snackbarHostState.showSnackbar(
                            message = "Video dibatasi. Mengalihkan ke YouTube...",
                            duration = SnackbarDuration.Indefinite
                        )
                    }
                    delay(1200)
                    snackbarJob.cancel()
                    openYouTubeAppOrWeb(context, effect.videoId)
                }
            }
        }
    }

    MovieDetailScreen(
        uiState = uiState,
        snackbarHostState = snackbarHostState,
        playingVideoId = playingVideoId,
        showReviewSheet = showReviewSheet,
        onBackClick = onBackClick,
        onRetry = { viewModel.onEvent(MovieDetailEvent.FetchMovieDetail) },
        onVideoClick = { videoId -> playingVideoId = videoId },
        onVideoDismiss = { playingVideoId = null },
        onVideoError = { videoId ->
            playingVideoId = null
            viewModel.onEvent(MovieDetailEvent.OnVideoError(videoId))
        },
        onReviewClick = { showReviewSheet = true },
        onReviewDismiss = { showReviewSheet = false }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovieDetailScreen(
    uiState: MovieDetailState,
    snackbarHostState: SnackbarHostState,
    playingVideoId: String?,
    showReviewSheet: Boolean,
    onBackClick: () -> Unit,
    onRetry: () -> Unit,
    onVideoClick: (String) -> Unit,
    onVideoDismiss: () -> Unit,
    onVideoError: (String) -> Unit,
    onReviewClick: () -> Unit,
    onReviewDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = Color(0xFF070D15),
        modifier = modifier
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = paddingValues.calculateBottomPadding())
                .background(Color(0xFF070D15))
        ) {
            when (uiState) {
                is MovieDetailState.Loading -> {
                    CircularProgressIndicator(
                        color = Color(0xFF00E5FF),
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                is MovieDetailState.Error -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = uiState.message, color = Color.White)
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = onRetry) {
                            Text("Coba Lagi")
                        }
                    }
                }

                is MovieDetailState.Success -> {
                    val movie = uiState.data

                    MovieDetailContent(
                        movie = movie,
                        onBackClick = onBackClick,
                        onVideoClick = onVideoClick,
                        onReviewClick = onReviewClick
                    )

                    if (showReviewSheet) {
                        ModalBottomSheet(
                            onDismissRequest = onReviewDismiss,
                            sheetState = sheetState,
                            containerColor = Color(0xFF111827),
                            dragHandle = null
                        ) {
                            Spacer(modifier = Modifier.height(16.dp))
                            ReviewBottomSheetContent(reviews = movie.reviews)
                        }
                    }

                    playingVideoId?.let { videoId ->
                        Dialog(
                            onDismissRequest = onVideoDismiss,
                            properties = DialogProperties(
                                dismissOnBackPress = true,
                                dismissOnClickOutside = true,
                                usePlatformDefaultWidth = false
                            )
                        ) {
                            Box(
                                modifier = Modifier.fillMaxWidth().padding(16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                YoutubePlayerComponent(
                                    videoId = videoId,
                                    onError = { onVideoError(videoId) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MovieDetailContent(
    movie: MovieDetailUiModel,
    onBackClick: () -> Unit,
    onVideoClick: (String) -> Unit,
    onReviewClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()
    val density = LocalDensity.current
    val titleScrollThresholdPx = remember(density) { with(density) { 350.dp.toPx() } }

    val showTitleInAppBar by remember {
        derivedStateOf {
            listState.firstVisibleItemIndex > 0 || listState.firstVisibleItemScrollOffset > titleScrollThresholdPx
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize()
        ) {
            item { HeroSection(backdropUrl = movie.backdropUrl) }

            item {
                MovieInfoSection(
                    title = movie.title,
                    genres = movie.genres,
                    overview = movie.overview
                )
            }

            if (movie.videos.isNotEmpty()) {
                item {
                    HeaderSection(title = "Videos")
                    VideoListSection(
                        videos = movie.videos,
                        onVideoClick = onVideoClick
                    )
                }
            }

            if (movie.reviews.isNotEmpty()) {
                item {
                    HeaderSection(
                        title = "Reviews",
                        seeAll = movie.reviews.size > 2,
                        onClick = { if (movie.reviews.size > 2) onReviewClick() }
                    )
                }
                items(
                    items = movie.reviews.take(2),
                    key = { "main_${it.id}" }
                ) { review ->
                    ReviewItem(review = review)
                    Spacer(modifier = Modifier.height(16.dp))
                }
                item { Spacer(modifier = Modifier.height(32.dp)) }
            }
        }

        StickyAppBar(
            title = movie.title,
            showTitle = showTitleInAppBar,
            onBackClick = onBackClick
        )
    }
}

private fun openYouTubeAppOrWeb(context: Context, videoId: String) {
    val appIntent = Intent(Intent.ACTION_VIEW, "vnd.youtube:$videoId".toUri())
    val webIntent = Intent(Intent.ACTION_VIEW, "https://www.youtube.com/watch?v=$videoId".toUri())
    try { context.startActivity(appIntent) } catch (_: ActivityNotFoundException) { context.startActivity(webIntent) }
}

@Preview(showBackground = true, showSystemUi = true, backgroundColor = 0xFF070D15)
@Composable
fun MovieDetailContentPreview() {
    val dummyMovie = MovieDetailUiModel(
        title = "Mercy",
        backdropUrl = "https://image.tmdb.org/t/p/w1280/pyok1kZJCfyuFapYXzHcy7BLlQa.jpg",
        genres = "Science Fiction | Action | Thriller",
        overview = "In the near future, a detective stands on trial accused of murdering his wife. He has ninety minutes to prove his innocence to the advanced AI Judge he once championed, before it determines his fate.",
        videos = listOf(
            VideoUiModel(
                id = "697663410407af056810665a",
                key = "xsyNtyjIFXg",
                name = "90 minutes to prove your innocence."
            ),
            VideoUiModel(
                id = "6979e1cfc7cbb99314cdbba9",
                key = "yKcwpiwp5vs",
                name = "Volume – Featurette"
            )
        ),
        reviews = listOf(
            ReviewUiModel(
                id = "697361a1c29174e95baee0e8",
                author = "Brent Marchant",
                avatarUrl = null,
                rating = 6.0,
                date = "2026-01-23",
                content = "Sometimes even the most ardent cinephiles need to take a break from serious movie watching with a big, dopey action-adventure thriller that has about as much meaningful substance as a supersized bucket of over-buttered popcorn."
            ),
            ReviewUiModel(
                id = "6975f1620f150cbbc8088896",
                author = "CinemaSerf",
                avatarUrl = null,
                rating = 6.0,
                date = "2026-01-25",
                content = "When detective “Raven” wakes up, he’s shocked to find himself strapped to a chair and somewhat hungover. All he has for company is the rather assertive image of “Judge Maddox” on a screen before him..."
            ),
            ReviewUiModel(
                id = "dummy_3",
                author = "Manuel São Bento",
                avatarUrl = null,
                rating = 4.0,
                date = "2026-02-02",
                content = "Mercy is an experience that sits between a fascinating premise and a careless execution, presenting a scenario of algorithmic justice that gets lost in narrative contradictions..."
            )
        )
    )

    MovieDetailScreen(
        uiState = MovieDetailState.Success(dummyMovie),
        snackbarHostState = SnackbarHostState(),
        playingVideoId = null,
        showReviewSheet = false,
        onBackClick = {},
        onRetry = {},
        onVideoClick = {},
        onVideoDismiss = {},
        onVideoError = {},
        onReviewClick = {},
        onReviewDismiss = {}
    )
}