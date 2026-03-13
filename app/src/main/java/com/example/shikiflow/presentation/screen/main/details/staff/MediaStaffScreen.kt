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
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.shikiflow.R
import com.example.shikiflow.domain.model.auth.AuthType
import com.example.shikiflow.domain.model.sort.StaffType
import com.example.shikiflow.domain.model.tracks.MediaType
import com.example.shikiflow.presentation.common.ErrorItem
import com.example.shikiflow.presentation.common.SortBottomSheet
import com.example.shikiflow.presentation.common.SortConfig
import com.example.shikiflow.presentation.screen.main.details.MediaNavOptions
import com.example.shikiflow.presentation.screen.main.details.common.StaffItem
import com.example.shikiflow.presentation.viewmodel.staff.MediaStaffViewModel

@Composable
fun MediaStaffScreen(
    mediaId: Int,
    mediaType: MediaType,
    authType: AuthType,
    navOptions: MediaNavOptions,
    mediaStaffViewModel: MediaStaffViewModel = hiltViewModel()
) {
    val mediaStaffItems = mediaStaffViewModel.mediaStaffItems.collectAsLazyPagingItems()
    val mediaStaffParams by mediaStaffViewModel.mediaStaffParams.collectAsStateWithLifecycle()

    var showBottomSheet by remember { mutableStateOf(false) }

    LaunchedEffect(mediaId) {
        mediaStaffViewModel.setParams(mediaId, mediaType)
    }

    Scaffold(
        floatingActionButton = {
            if(authType == AuthType.ANILIST) {
                FloatingActionButton(
                    onClick = { showBottomSheet = true },
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.onSurface
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_sort),
                        contentDescription = "Show Sort Bottom Sheet"
                    )
                }
            }
        }
    ) { paddingValues ->
        when(mediaStaffItems.loadState.refresh) {
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
                        onButtonClick = { mediaStaffItems.retry() }
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
                    items(mediaStaffItems.itemCount) { index ->
                        mediaStaffItems[index]?.let { staffShort ->
                            StaffItem(
                                staffShort = staffShort,
                                onStaffClick = { staffId ->
                                    navOptions.navigateToStaff(staffId)
                                }
                            )
                        }
                    }
                    mediaStaffItems.apply {
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
                                    onButtonClick = { mediaStaffItems.retry() }
                                )
                            }
                        }
                    }
                }
            }
        }
        if(showBottomSheet) {
            SortBottomSheet(
                config = SortConfig<StaffType>(
                    options = StaffType.entries,
                    selected = mediaStaffParams.staffSort,
                    onSortChange = { sort ->
                        mediaStaffViewModel.setSort(sort)
                    }
                ),
                onDismiss = { showBottomSheet = false }
            )
        }
    }
}