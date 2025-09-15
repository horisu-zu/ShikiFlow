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
import com.example.shikiflow.BuildConfig
import com.example.shikiflow.domain.model.tracks.MediaType
import com.example.shikiflow.domain.model.tracks.TargetType
import com.example.shikiflow.domain.model.tracks.toUiModel
import com.example.shikiflow.presentation.common.UserRateBottomSheet
import com.example.shikiflow.presentation.screen.MediaNavOptions
import com.example.shikiflow.presentation.viewmodel.manga.MangaDetailsViewModel
import com.example.shikiflow.presentation.viewmodel.user.UserViewModel
import com.example.shikiflow.utils.Converter.EntityType
import com.example.shikiflow.utils.Resource
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.core.net.toUri
import com.example.shikiflow.R
import com.example.shikiflow.presentation.common.ErrorItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MangaDetailsScreen(
    id: String,
    currentUser: CurrentUserQuery.Data?,
    navOptions: MediaNavOptions,
    mangaDetailsViewModel: MangaDetailsViewModel = hiltViewModel(),
    userViewModel: UserViewModel = hiltViewModel()
) {
    val mangaDetails = mangaDetailsViewModel.mangaDetails.collectAsState()
    val mangaDexIds = mangaDetailsViewModel.mangaDexIds.collectAsState().value

    var rateBottomSheet by remember { mutableStateOf(false) }
    var isRefreshing by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    val customTabIntent = CustomTabsIntent.Builder().build()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        userViewModel.updateEvent.collect {
            mangaDetailsViewModel.getMangaDetails(id, isRefresh = true)
            rateBottomSheet = false
        }
    }

    LaunchedEffect(id) {
        mangaDetailsViewModel.getMangaDetails(id)
    }

    Scaffold(
        topBar = { /*TODO*/ }
    ) { paddingValues ->
        when (mangaDetails.value) {
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
                            mangaDetails = mangaDetails.value.data,
                            mangaDexResource = mangaDexIds,
                            onStatusClick = { rateBottomSheet = true },
                            onMangaDexNavigateClick = { title ->
                                navOptions.navigateToMangaRead(
                                    mangaDexIds = mangaDexIds.data ?: emptyList(),
                                    title = title,
                                    completedChapters = mangaDetails.value.data
                                        ?.userRate?.chapters ?: 0
                                )
                            },
                            onMangaDexRefreshClick = { mangaDetailsViewModel.getMangaDetails(id) }
                        )

                        MangaDetailsDesc(
                            mangaDetails = mangaDetails.value.data,
                            onItemClick = { id, mediaType ->
                                if(mediaType == MediaType.ANIME) {
                                    navOptions.navigateToAnimeDetails(id)
                                } else navOptions.navigateToMangaDetails(id)
                            },
                            onEntityClick = { entityType, id ->
                                when(entityType) {
                                    EntityType.CHARACTER -> {
                                        navOptions.navigateToCharacterDetails(id)
                                    }
                                    EntityType.PERSON -> {
                                        customTabIntent.launchUrl(context,
                                            "${BuildConfig.BASE_URL}/person/$id".toUri()
                                        )
                                    }
                                    EntityType.ANIME -> {
                                        navOptions.navigateToAnimeDetails(id)
                                    }
                                    EntityType.MANGA -> {
                                        navOptions.navigateToMangaDetails(id)
                                    }
                                    EntityType.COMMENT -> { /**/ }
                                }
                            },
                            onLinkClick = { url ->
                                customTabIntent.launchUrl(context, url.toUri())
                            },
                            modifier = Modifier.padding(12.dp)
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
        val isUpdating by userViewModel.isUpdating.collectAsState()
        val mangaDetailsData = mangaDetails.value.data

        mangaDetailsData?.let {
            UserRateBottomSheet(
                userRate = mangaDetailsData.toUiModel(),
                isLoading = isUpdating,
                onDismiss = { rateBottomSheet = false },
                onSave = { rateId, status, score, episodes, rewatches, mediaType ->
                    userViewModel.updateUserRate(
                        id = rateId,
                        status = status,
                        score = score,
                        progress = episodes,
                        rewatches = rewatches,
                        mediaType = mediaType
                    )
                },
                onCreateRate = { mediaId, status ->
                    userViewModel.createUserRate(
                        userId = currentUser?.currentUser?.id ?: "",
                        targetId = mediaId,
                        status = status,
                        targetType = TargetType.MANGA
                    )
                }
            )
        }
    }
}