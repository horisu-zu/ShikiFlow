package com.example.shikiflow.presentation.screen.main.details.anime

import android.util.Log
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.foundation.layout.Box
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
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.graphql.CurrentUserQuery
import com.example.shikiflow.BuildConfig
import com.example.shikiflow.data.tracks.MediaType
import com.example.shikiflow.data.tracks.TargetType
import com.example.shikiflow.data.tracks.UserRateData
import com.example.shikiflow.data.tracks.toUiModel
import com.example.shikiflow.presentation.common.UserRateBottomSheet
import com.example.shikiflow.presentation.screen.MediaNavOptions
import com.example.shikiflow.presentation.viewmodel.anime.AnimeDetailsViewModel
import com.example.shikiflow.presentation.viewmodel.user.UserViewModel
import com.example.shikiflow.utils.Converter.EntityType
import com.example.shikiflow.utils.Resource
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnimeDetailsScreen(
    id: String,
    currentUser: CurrentUserQuery.Data?,
    navOptions: MediaNavOptions,
    animeDetailsViewModel: AnimeDetailsViewModel = hiltViewModel(),
    userViewModel: UserViewModel = hiltViewModel()
) {
    val animeDetails = animeDetailsViewModel.animeDetails.collectAsState()
    var rateBottomSheet by remember { mutableStateOf(false) }
    var isRefreshing by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    val customTabIntent = CustomTabsIntent.Builder().build()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        userViewModel.updateEvent.collect { response ->
            animeDetailsViewModel.getAnimeDetails(id, isRefresh = true)
            rateBottomSheet = false
        }
    }

    LaunchedEffect(id) {
        animeDetailsViewModel.getAnimeDetails(id)
    }

    Scaffold(
        topBar = { /**/ }
    ) { paddingValues ->
        when (animeDetails.value) {
            is Resource.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Log.d("Details Screen", "Loading Anime ID: $id")
                    CircularProgressIndicator()
                }
            }

            is Resource.Success -> {
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
                    ConstraintLayout(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(
                                start = paddingValues.calculateStartPadding(LayoutDirection.Ltr),
                                end = paddingValues.calculateEndPadding(LayoutDirection.Ltr),
                            ).verticalScroll(rememberScrollState())
                    ) {
                        val (titleRef, descriptionRef) = createRefs()

                        AnimeDetailsTitle(
                            animeDetails = animeDetails.value.data,
                            onStatusClick = { rateBottomSheet = true },
                            modifier = Modifier.constrainAs(titleRef) {
                                top.linkTo(parent.top)
                                start.linkTo(parent.start)
                                end.linkTo(parent.end)
                                bottom.linkTo(descriptionRef.top)
                            }
                        )

                        AnimeDetailsDesc(
                            animeDetails = animeDetails.value.data,
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
                                }
                            },
                            onSimilarClick = { animeId, title ->
                                navOptions.navigateToSimilarPage(id, title, MediaType.ANIME)
                            },
                            onLinksClick = { animeId ->
                                navOptions.navigateToLinksPage(id, MediaType.ANIME)
                            },
                            modifier = Modifier.constrainAs(descriptionRef) {
                                top.linkTo(titleRef.bottom, margin = 12.dp)
                            }.padding(horizontal = 12.dp)
                        )
                    }
                }
            }

            is Resource.Error -> { /*TODO*/ }
        }
    }

    if (rateBottomSheet) {
        val isUpdating by userViewModel.isUpdating.collectAsState()
        val animeDetailsData = animeDetails.value.data

        UserRateBottomSheet(
            userRate = animeDetailsData?.userRate?.toUiModel(animeDetailsData) ?: UserRateData.createEmpty(
                mediaId = animeDetailsData?.id ?: "",
                mediaTitle = animeDetailsData?.name ?: "",
                mediaPosterUrl = animeDetailsData?.poster?.originalUrl ?: "",
                mediaType = MediaType.ANIME
            ),
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
                    targetType = TargetType.ANIME
                )
            }
        )
    }
}