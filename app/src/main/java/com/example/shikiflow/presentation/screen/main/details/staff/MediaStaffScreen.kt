package com.example.shikiflow.presentation.screen.main.details.staff

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.shikiflow.R
import com.example.shikiflow.domain.model.tracks.MediaType
import com.example.shikiflow.presentation.common.ErrorItem
import com.example.shikiflow.presentation.screen.main.details.MediaNavOptions
import com.example.shikiflow.presentation.screen.main.details.common.StaffItem
import com.example.shikiflow.presentation.viewmodel.staff.MediaStaffViewModel

@Composable
fun MediaStaffScreen(
    mediaId: Int,
    mediaType: MediaType,
    navOptions: MediaNavOptions,
    mediaStaffViewModel: MediaStaffViewModel = hiltViewModel()
) {
    val mediaStaff = mediaStaffViewModel.getMediaStaff(
        mediaId = mediaId,
        mediaType = mediaType
    ).collectAsLazyPagingItems()

    Scaffold { paddingValues ->
        when(mediaStaff.loadState.refresh) {
            is LoadState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) { CircularProgressIndicator() }
            }
            is LoadState.Error -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    ErrorItem(
                        message = stringResource(R.string.common_error),
                        buttonLabel = stringResource(R.string.common_retry),
                        onButtonClick = { mediaStaff.retry() }
                    )
                }
            }
            else -> {
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(240.dp),
                    modifier = Modifier.fillMaxSize()
                        .padding(
                            start = paddingValues.calculateStartPadding(LayoutDirection.Ltr),
                            end = paddingValues.calculateEndPadding(LayoutDirection.Ltr)
                        ),
                    contentPadding = PaddingValues(
                        top = WindowInsets.statusBars.asPaddingValues().calculateTopPadding(),
                        bottom = 8.dp,
                        start = 12.dp,
                        end = 12.dp
                    ),
                    horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.Start),
                    verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.Top)
                ) {
                    items(mediaStaff.itemCount) { index ->
                        mediaStaff[index]?.let { staffShort ->
                            StaffItem(
                                staffShort = staffShort,
                                onStaffClick = { staffId ->
                                    navOptions.navigateToStaff(staffId)
                                }
                            )
                        }
                    }
                    mediaStaff.apply {
                        if(loadState.append is LoadState.Loading) {
                            item(
                                span = { GridItemSpan(maxLineSpan) }
                            ) {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) { CircularProgressIndicator() }
                            }
                        } else if(loadState.append is LoadState.Error) {
                            item(
                                span = { GridItemSpan(maxLineSpan) }
                            ) {
                                ErrorItem(
                                    message = stringResource(R.string.common_error),
                                    showFace = false,
                                    buttonLabel = stringResource(R.string.common_retry),
                                    onButtonClick = { mediaStaff.retry() }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}