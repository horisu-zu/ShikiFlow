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
import com.example.shikiflow.utils.Resource
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
    val mangaDetails by mangaDetailsViewModel.mangaDetails.collectAsStateWithLifecycle()
    val mangaDexIds by mangaDetailsViewModel.mangaDexIds.collectAsStateWithLifecycle()
    val rateUpdateState by mangaDetailsViewModel.rateUpdateState
    val isRefreshing by mangaDetailsViewModel.isRefreshing

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
                    onRefresh = { mangaDetailsViewModel.getMangaDetails(id, isRefresh = true) }
                ) {
                    mangaDetails.data?.let { mangaDetails ->
                        MangaDetailsContent(
                            userId = userId?.toInt() ?: 0,
                            mangaDetails = mangaDetails,
                            mangaDexResource = mangaDexIds,
                            rateUpdateState = rateUpdateState,
                            mediaNavOptions = navOptions,
                            onMangaDexRefreshClick = {
                                mangaDetailsViewModel.getMangaDexId(
                                    title = mangaDetails.title,
                                    nativeTitle = mangaDetails.native,
                                    malId = mangaDetails.malId
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
}