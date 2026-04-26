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
import com.example.shikiflow.presentation.common.ErrorItem
import com.example.shikiflow.presentation.common.FullScreenImageDialog
import com.example.shikiflow.presentation.screen.main.details.MediaNavOptions
import com.example.shikiflow.presentation.viewmodel.anime.details.AnimeDetailsViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
fun AnimeDetailsScreen(
    id: Int,
    navOptions: MediaNavOptions,
    animeDetailsViewModel: AnimeDetailsViewModel = hiltViewModel()
) {
    val uiState by animeDetailsViewModel.uiState.collectAsStateWithLifecycle()
    var selectedScreenshotIndex by remember { mutableStateOf<Int?>(null) }

    LaunchedEffect(id) {
        animeDetailsViewModel.setMediaId(id)
    }

    Scaffold { paddingValues ->
        if(uiState.isLoading && uiState.details == null) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) { CircularProgressIndicator() }
        } else if(uiState.errorMessage != null) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                ErrorItem(
                    message = stringResource(id = R.string.details_error),
                    buttonLabel = stringResource(R.string.common_retry),
                    onButtonClick = { animeDetailsViewModel.onRefresh() }
                )
            }
        } else {
            SharedTransitionLayout {
                AnimatedVisibility(
                    visible = selectedScreenshotIndex != null,
                    modifier = Modifier.fillMaxSize().zIndex(1f)
                ) {
                    uiState.details?.let { details ->
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
                uiState.details?.let { details ->
                    PullToRefreshBox(
                        isRefreshing = uiState.isRefreshing,
                        onRefresh = { animeDetailsViewModel.onRefresh() }
                    ) {
                        uiState.authType?.let { authType ->
                            AnimeDetailsContent(
                                currentAuthType = authType,
                                animeDetails = details,
                                rateUpdateState = uiState.rateUpdateState,
                                sharedTransitionScope = this@SharedTransitionLayout,
                                selectedScreenshotIndex = selectedScreenshotIndex,
                                onScreenshotClick = { index ->
                                    selectedScreenshotIndex = index
                                },
                                onSaveUserRate = { save, shortData ->
                                    animeDetailsViewModel.saveUserRate(
                                        saveUserRate = save,
                                        mediaShortData = shortData
                                    )
                                },
                                onDeleteUserRate = { entryId ->
                                    animeDetailsViewModel.deleteUserRate(
                                        entryId = entryId,
                                        mediaId = details.id,
                                        malId = details.malId,
                                        mediaType = details.mediaType
                                    )
                                },
                                onToggleFavorite = {
                                    animeDetailsViewModel.toggleFavorite(details.id)
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
}