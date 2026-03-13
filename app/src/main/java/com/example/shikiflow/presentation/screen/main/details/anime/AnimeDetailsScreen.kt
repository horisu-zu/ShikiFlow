package com.example.shikiflow.presentation.screen.main.details.anime

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.zIndex
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.shikiflow.R
import com.example.shikiflow.domain.model.auth.AuthType
import com.example.shikiflow.presentation.common.ErrorItem
import com.example.shikiflow.presentation.common.FullScreenImageDialog
import com.example.shikiflow.presentation.screen.main.details.MediaNavOptions
import com.example.shikiflow.presentation.viewmodel.anime.AnimeDetailsViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
fun AnimeDetailsScreen(
    id: Int,
    userId: String?,
    authType: AuthType,
    navOptions: MediaNavOptions,
    animeDetailsViewModel: AnimeDetailsViewModel = hiltViewModel()
) {
    val animeDetails by animeDetailsViewModel.animeDetails.collectAsStateWithLifecycle()
    var selectedScreenshotIndex by remember { mutableStateOf<Int?>(null) }

    LaunchedEffect(id) {
        animeDetailsViewModel.getAnimeDetails(id)
    }

    Scaffold { paddingValues ->
        if(animeDetails.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) { CircularProgressIndicator() }
        } else if(animeDetails.detailsError != null) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                ErrorItem(
                    message = stringResource(
                        id = R.string.details_error,
                        R.string.media_type_anime
                    ),
                    buttonLabel = stringResource(R.string.common_retry),
                    onButtonClick = { animeDetailsViewModel.getAnimeDetails(id, isRefresh = true) }
                )
            }
        } else {
            SharedTransitionLayout {
                AnimatedVisibility(
                    visible = selectedScreenshotIndex != null,
                    modifier = Modifier.fillMaxSize().zIndex(1f)
                ) {
                    animeDetails.details?.let { details ->
                        FullScreenImageDialog(
                            imageUrls = details.screenshots,
                            initialIndex = selectedScreenshotIndex ?: 0,
                            visibilityScope = this@AnimatedVisibility,
                            onImageChange = { index ->
                                selectedScreenshotIndex = index
                            }
                        )
                    }
                }
                PullToRefreshBox(
                    isRefreshing = animeDetails.isRefreshing,
                    onRefresh = { animeDetailsViewModel.getAnimeDetails(id, isRefresh = true) }
                ) {
                    animeDetails.details?.let { details ->
                        AnimeDetailsContent(
                            userId = userId?.toInt() ?: 0,
                            currentAuthType = authType,
                            animeDetails = details,
                            rateUpdateState = animeDetails.rateUpdateState,
                            sharedTransitionScope = this@SharedTransitionLayout,
                            selectedScreenshotIndex = selectedScreenshotIndex,
                            onScreenshotClick = { index ->
                                selectedScreenshotIndex = index
                            },
                            onSaveUserRate = { id, save, shortData ->
                                animeDetailsViewModel.saveUserRate(
                                    userId = id,
                                    saveUserRate = save,
                                    animeShortData = shortData
                                )
                            },
                            mediaNavOptions = navOptions,
                            modifier = Modifier.padding(
                                start = paddingValues.calculateStartPadding(LayoutDirection.Ltr),
                                end = paddingValues.calculateEndPadding(LayoutDirection.Ltr),
                            )
                        )
                    }
                }
            }
        }
    }
}