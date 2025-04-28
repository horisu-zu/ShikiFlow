package com.example.shikiflow.presentation.screen.main.details.manga

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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.graphql.CurrentUserQuery
import com.example.shikiflow.data.tracks.MediaType
import com.example.shikiflow.data.tracks.TargetType
import com.example.shikiflow.data.tracks.UserRateData
import com.example.shikiflow.data.tracks.toUiModel
import com.example.shikiflow.presentation.common.UserRateBottomSheet
import com.example.shikiflow.presentation.screen.MainNavRoute
import com.example.shikiflow.presentation.screen.MediaNavOptions
import com.example.shikiflow.presentation.viewmodel.manga.MangaDetailsViewModel
import com.example.shikiflow.presentation.viewmodel.user.UserViewModel
import com.example.shikiflow.utils.Resource
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

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
    var rateBottomSheet by remember { mutableStateOf(false) }
    var isRefreshing by remember { mutableStateOf(false) }
    val isInitialized = rememberSaveable { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        userViewModel.updateEvent.collect {
            mangaDetailsViewModel.getMangaDetails(id, isRefresh = true)
            rateBottomSheet = false
        }
    }

    LaunchedEffect(id) {
        if(!isInitialized.value) {
            mangaDetailsViewModel.getMangaDetails(id)
            isInitialized.value = true
        }
    }

    Scaffold(
        topBar = { /*TODO*/ }
    ) { paddingValues ->
        when (mangaDetails.value) {
            is Resource.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
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
                                mangaDetailsViewModel.getMangaDetails(id, isRefresh = true)
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
                        val (headerRef, descriptionRef) = createRefs()

                        MangaDetailsHeader(
                            mangaDetails = mangaDetails.value.data,
                            onStatusClick = { rateBottomSheet = true },
                            modifier = Modifier.constrainAs(headerRef) {
                                top.linkTo(parent.top)
                                start.linkTo(parent.start)
                                end.linkTo(parent.end)
                                bottom.linkTo(descriptionRef.top)
                            }
                        )

                        MangaDetailsDesc(
                            mangaDetails = mangaDetails.value.data,
                            onItemClick = { id, mediaType ->
                                if(mediaType == MediaType.ANIME) {
                                    navOptions.navigateToAnimeDetails(id)
                                } else navOptions.navigateToMangaDetails(id)
                            },
                            onCharacterClick = { characterId ->
                                navOptions.navigateToCharacterDetails(characterId)
                            },
                            modifier = Modifier.constrainAs(descriptionRef) {
                                top.linkTo(headerRef.bottom)
                                start.linkTo(parent.start)
                                end.linkTo(parent.end)
                            }.padding(12.dp)
                        )
                    }
                }
            }
            is Resource.Error -> { /*TODO*/ }
        }
    }

    if(rateBottomSheet) {
        val isUpdating by userViewModel.isUpdating.collectAsState()
        val mangaDetailsData = mangaDetails.value.data

        UserRateBottomSheet(
            userRate = mangaDetailsData?.userRate?.toUiModel(mangaDetailsData) ?: UserRateData.createEmpty(
                mediaId = mangaDetailsData?.id ?: "",
                mediaTitle = mangaDetailsData?.name ?: "",
                mediaPosterUrl = mangaDetailsData?.poster?.posterShort?.originalUrl ?: "",
                mediaType = MediaType.MANGA
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
                    targetType = TargetType.MANGA
                )
            }
        )
    }
}