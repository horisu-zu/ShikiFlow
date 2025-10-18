package com.example.shikiflow.presentation.screen.main.details.anime

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.shikiflow.R
import com.example.shikiflow.domain.model.tracks.MediaType
import com.example.shikiflow.domain.model.tracks.toUiModel
import com.example.shikiflow.presentation.common.ErrorItem
import com.example.shikiflow.presentation.common.FullScreenImageDialog
import com.example.shikiflow.presentation.common.UserRateBottomSheet
import com.example.shikiflow.presentation.screen.main.details.MediaNavOptions
import com.example.shikiflow.presentation.screen.main.details.common.CommentsScreenMode
import com.example.shikiflow.presentation.viewmodel.anime.AnimeDetailsViewModel
import com.example.shikiflow.utils.Resource
import com.example.shikiflow.utils.WebIntent

@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
fun AnimeDetailsScreen(
    id: String,
    userId: String?,
    navOptions: MediaNavOptions,
    animeDetailsViewModel: AnimeDetailsViewModel = hiltViewModel()
) {
    val animeDetails by animeDetailsViewModel.animeDetails.collectAsStateWithLifecycle()
    val rateUpdateState by animeDetailsViewModel.rateUpdateState
    val isRefreshing by animeDetailsViewModel.isRefreshing

    var selectedScreenshotIndex by remember { mutableStateOf<Int?>(null) }
    var rateBottomSheet by remember { mutableStateOf(false) }
    val context = LocalContext.current

    LaunchedEffect(id) {
        animeDetailsViewModel.getAnimeDetails(id)
    }

    Scaffold { paddingValues ->
        when (animeDetails) {
            is Resource.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) { CircularProgressIndicator() }
            }

            is Resource.Success -> {
                SharedTransitionLayout {
                    AnimatedVisibility(
                        visible = selectedScreenshotIndex != null,
                        enter = fadeIn(),
                        exit = fadeOut(),
                        modifier = Modifier.zIndex(1f)
                    ) {
                        animeDetails.data?.let { details ->
                            selectedScreenshotIndex?.let { index ->
                                FullScreenImageDialog(
                                    imageUrls = details.screenshots.map { it.originalUrl },
                                    initialIndex = index,
                                    visibilityScope = this@AnimatedVisibility,
                                    onDismiss = { selectedScreenshotIndex = null }
                                )
                            }
                        }
                    }
                    PullToRefreshBox(
                        isRefreshing = isRefreshing,
                        onRefresh = { animeDetailsViewModel.getAnimeDetails(id, isRefresh = true) }
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize()
                                .padding(
                                    start = paddingValues.calculateStartPadding(LayoutDirection.Ltr),
                                    end = paddingValues.calculateEndPadding(LayoutDirection.Ltr),
                                ).verticalScroll(rememberScrollState()),
                            verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.Top)
                        ) {
                            animeDetails.data?.let { details ->
                                AnimeDetailsTitle(
                                    animeDetails = details,
                                    onStatusClick = { rateBottomSheet = true },
                                    onPlayClick = { title, id, completedEpisodes ->
                                        navOptions.navigateToAnimeWatch(title, id, completedEpisodes)
                                    }
                                )
                                AnimeDetailsDesc(
                                    animeDetails = details,
                                    selectedScreenshotIndex = selectedScreenshotIndex,
                                    sharedTransitionScope = this@SharedTransitionLayout,
                                    isRefreshing = isRefreshing,
                                    onStudioClick = { id, studioName ->
                                        navOptions.navigateToStudio(id, studioName)
                                    },
                                    onItemClick = { id, mediaType ->
                                        if (mediaType == MediaType.ANIME) {
                                            navOptions.navigateToAnimeDetails(id)
                                        } else navOptions.navigateToMangaDetails(id)
                                    },
                                    onEntityClick = { entityType, id ->
                                        navOptions.navigateByEntity(entityType, id)
                                    },
                                    onLinkClick = { url ->
                                        WebIntent.openUrlCustomTab(context, url)
                                    },
                                    onTopicNavigate = { topicId ->
                                        navOptions.navigateToComments(CommentsScreenMode.TOPIC, topicId)
                                    },
                                    onSimilarClick = { animeId, title ->
                                        navOptions.navigateToSimilarPage(id, title, MediaType.ANIME)
                                    },
                                    onExternalLinksClick = { animeId ->
                                        navOptions.navigateToLinksPage(id, MediaType.ANIME)
                                    },
                                    onScreenshotClick = { index ->
                                        selectedScreenshotIndex = index
                                    }
                                )
                            }
                        }
                    }
                }
            }
            is Resource.Error -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    ErrorItem(
                        message = stringResource(
                            id = R.string.details_error,
                            R.string.browse_search_media_anime
                        ),
                        buttonLabel = stringResource(R.string.common_retry),
                        onButtonClick = { animeDetailsViewModel.getAnimeDetails(id, isRefresh = true) }
                    )
                }
            }
        }
    }

    animeDetails.data?.let { details ->
        if (rateBottomSheet) {
            UserRateBottomSheet(
                userRate = details.toUiModel(),
                rateUpdateState = rateUpdateState,
                onDismiss = { rateBottomSheet = false },
                onSave = { rateId, status, score, episodes, rewatches ->
                    animeDetailsViewModel.updateUserRate(
                        id = rateId,
                        status = status,
                        score = score,
                        progress = episodes,
                        rewatches = rewatches
                    )
                },
                onCreateRate = { mediaId, status ->
                    userId?.let {
                        animeDetailsViewModel.createUserRate(
                            userId = userId,
                            targetId = mediaId,
                            status = status
                        )
                    }
                }
            )
        }
    }
}