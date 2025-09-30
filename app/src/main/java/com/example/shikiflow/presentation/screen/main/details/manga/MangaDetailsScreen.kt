package com.example.shikiflow.presentation.screen.main.details.manga

import androidx.browser.customtabs.CustomTabsIntent
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
import androidx.compose.runtime.collectAsState
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
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.graphql.CurrentUserQuery
import com.example.shikiflow.domain.model.tracks.MediaType
import com.example.shikiflow.domain.model.tracks.toUiModel
import com.example.shikiflow.presentation.common.UserRateBottomSheet
import com.example.shikiflow.presentation.screen.main.details.MediaNavOptions
import com.example.shikiflow.presentation.viewmodel.manga.MangaDetailsViewModel
import com.example.shikiflow.utils.Resource
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.core.net.toUri
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.shikiflow.R
import com.example.shikiflow.presentation.common.ErrorItem
import com.example.shikiflow.presentation.screen.main.details.common.CommentsScreenMode

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MangaDetailsScreen(
    id: String,
    currentUser: CurrentUserQuery.Data?,
    navOptions: MediaNavOptions,
    mangaDetailsViewModel: MangaDetailsViewModel = hiltViewModel()
) {
    val mangaDetails by mangaDetailsViewModel.mangaDetails.collectAsStateWithLifecycle()
    val mangaDexIds by mangaDetailsViewModel.mangaDexIds.collectAsStateWithLifecycle()

    val horizontalPadding = 12.dp
    var rateBottomSheet by remember { mutableStateOf(false) }
    var isRefreshing by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    val customTabIntent = CustomTabsIntent.Builder().build()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        mangaDetailsViewModel.updateEvent.collect {
            rateBottomSheet = false
        }
    }

    LaunchedEffect(id) {
        mangaDetailsViewModel.getMangaDetails(id)
    }

    Scaffold { paddingValues ->
        when (mangaDetails) {
            is Resource.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) { CircularProgressIndicator() }
            }
            is Resource.Success -> {
                PullToRefreshBox(
                    isRefreshing = isRefreshing,
                    onRefresh = {
                        coroutineScope.launch {
                            try {
                                isRefreshing = true
                                delay(300)
                                mangaDetailsViewModel.getMangaDetails(id, isRefresh = true)
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
                            ).verticalScroll(rememberScrollState())
                    ) {
                        MangaDetailsHeader(
                            mangaDetails = mangaDetails.data,
                            mangaDexResource = mangaDexIds,
                            horizontalPadding = horizontalPadding,
                            onStatusClick = { rateBottomSheet = true },
                            onMangaDexNavigateClick = { title ->
                                navOptions.navigateToMangaRead(
                                    mangaDexIds = mangaDexIds.data ?: emptyList(),
                                    title = title,
                                    completedChapters = mangaDetails.data
                                        ?.userRate?.chapters ?: 0
                                )
                            },
                            onMangaDexRefreshClick = { mangaDetailsViewModel.getMangaDetails(id) }
                        )

                        MangaDetailsDesc(
                            mangaDetails = mangaDetails.data,
                            horizontalPadding = horizontalPadding,
                            isRefreshing = isRefreshing,
                            onItemClick = { id, mediaType ->
                                if(mediaType == MediaType.ANIME) {
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
                            modifier = Modifier.padding(vertical = 12.dp)
                        )
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
                            R.string.browse_search_media_manga
                        ),
                        buttonLabel = stringResource(R.string.common_retry),
                        onButtonClick = { mangaDetailsViewModel.getMangaDetails(id) }
                    )
                }
            }
        }
    }

    if(rateBottomSheet) {
        val isUpdating by mangaDetailsViewModel.isUpdating.collectAsState()
        val mangaDetailsData = mangaDetails.data

        mangaDetailsData?.let {
            UserRateBottomSheet(
                userRate = mangaDetailsData.toUiModel(),
                isLoading = isUpdating,
                onDismiss = { rateBottomSheet = false },
                onSave = { rateId, status, score, episodes, rewatches ->
                    mangaDetailsViewModel.updateUserRate(
                        id = rateId,
                        status = status,
                        score = score,
                        progress = episodes,
                        rewatches = rewatches
                    )
                },
                onCreateRate = { mediaId, status ->
                    currentUser?.currentUser?.id?.let { userId ->
                        mangaDetailsViewModel.createUserRate(
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