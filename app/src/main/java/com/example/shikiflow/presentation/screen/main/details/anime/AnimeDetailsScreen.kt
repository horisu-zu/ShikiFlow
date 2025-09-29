package com.example.shikiflow.presentation.screen.main.details.anime

import androidx.browser.customtabs.CustomTabsIntent
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.core.net.toUri
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.graphql.CurrentUserQuery
import com.example.shikiflow.R
import com.example.shikiflow.domain.model.tracks.MediaType
import com.example.shikiflow.domain.model.tracks.toUiModel
import com.example.shikiflow.presentation.common.ErrorItem
import com.example.shikiflow.presentation.common.FullScreenImageDialog
import com.example.shikiflow.presentation.common.UserRateBottomSheet
import com.example.shikiflow.presentation.screen.MediaNavOptions
import com.example.shikiflow.presentation.screen.main.details.common.CommentsScreenMode
import com.example.shikiflow.presentation.viewmodel.anime.AnimeDetailsViewModel
import com.example.shikiflow.utils.Resource
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
fun AnimeDetailsScreen(
    id: String,
    currentUser: CurrentUserQuery.Data?,
    navOptions: MediaNavOptions,
    animeDetailsViewModel: AnimeDetailsViewModel = hiltViewModel()
) {
    val animeDetails by animeDetailsViewModel.animeDetails.collectAsStateWithLifecycle()

    var selectedScreenshotIndex by remember { mutableStateOf<Int?>(null) }
    var rateBottomSheet by remember { mutableStateOf(false) }
    var isRefreshing by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    val customTabIntent = CustomTabsIntent.Builder().build()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        animeDetailsViewModel.updateEvent.collect { response ->
            rateBottomSheet = false
        }
    }

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
                        onRefresh = {
                            coroutineScope.launch {
                                try {
                                    isRefreshing = true
                                    delay(300)
                                    animeDetailsViewModel.getAnimeDetails(id, isRefresh = true)
                                } finally {
                                    isRefreshing = false
                                }
                            }
                        }
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
                                    context = context,
                                    onPlayClick = { title, id, completedEpisodes ->
                                        navOptions.navigateToAnimeWatch(title, id, completedEpisodes)
                                    }
                                )
                                AnimeDetailsDesc(
                                    animeDetails = details,
                                    selectedScreenshotIndex = selectedScreenshotIndex,
                                    sharedTransitionScope = this@SharedTransitionLayout,
                                    isRefreshing = isRefreshing,
                                    context = context,
                                    onItemClick = { id, mediaType ->
                                        if (mediaType == MediaType.ANIME) {
                                            navOptions.navigateToAnimeDetails(id)
                                        } else navOptions.navigateToMangaDetails(id)
                                    },
                                    onEntityClick = { entityType, id ->
                                        navOptions.navigateByEntity(entityType, id)
                                    },
                                    onLinkClick = { url ->
                                        customTabIntent.launchUrl(context, url.toUri())
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

    if (rateBottomSheet) {
        val isUpdating by animeDetailsViewModel.isUpdating.collectAsStateWithLifecycle()

        animeDetails.data?.let { details ->
            UserRateBottomSheet(
                userRate = details.toUiModel(),
                isLoading = isUpdating,
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
                    currentUser?.currentUser?.id?.let { userId ->
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