package com.example.shikiflow.presentation.screen.main.details.manga

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.LayoutDirection
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.shikiflow.presentation.screen.main.details.MediaNavOptions
import com.example.shikiflow.presentation.viewmodel.manga.MangaDetailsViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.shikiflow.R
import com.example.shikiflow.domain.model.auth.AuthType
import com.example.shikiflow.presentation.common.ErrorItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MangaDetailsScreen(
    id: Int,
    authType: AuthType,
    userId: String?,
    navOptions: MediaNavOptions,
    mangaDetailsViewModel: MangaDetailsViewModel = hiltViewModel()
) {
    val mangaDetails by mangaDetailsViewModel.details.collectAsStateWithLifecycle()

    LaunchedEffect(id) {
        mangaDetailsViewModel.getMangaDetails(id)
    }

    Scaffold { paddingValues ->
        if(mangaDetails.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if(mangaDetails.detailsError != null) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                ErrorItem(
                    message = stringResource(id = R.string.details_error, R.string.media_type_manga),
                    buttonLabel = stringResource(R.string.common_retry),
                    onButtonClick = { mangaDetailsViewModel.getMangaDetails(id) }
                )
            }
        } else {
            mangaDetails.details?.let { details ->
                PullToRefreshBox(
                    isRefreshing = mangaDetails.isRefreshing,
                    onRefresh = { mangaDetailsViewModel.getMangaDetails(id, isRefresh = true) }
                ) {
                    MangaDetailsContent(
                        userId = userId?.toInt() ?: 0,
                        authType = authType,
                        mangaDetails = details,
                        mangaDexResource = mangaDetails.mangaDexIds,
                        rateUpdateState = mangaDetails.rateUpdateState,
                        mediaNavOptions = navOptions,
                        onMangaDexRefreshClick = {
                            mangaDetailsViewModel.getMangaDexId(
                                title = details.title,
                                nativeTitle = details.native,
                                malId = details.malId
                            )
                        },
                        onSaveUserRate = { id, save, shortData ->
                            mangaDetailsViewModel.saveUserRate(
                                userId = id,
                                saveUserRate = save,
                                mangaShortData = shortData
                            )
                        },
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