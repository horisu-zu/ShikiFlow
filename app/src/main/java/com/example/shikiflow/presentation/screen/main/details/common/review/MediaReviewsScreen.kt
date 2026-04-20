package com.example.shikiflow.presentation.screen.main.details.common.review

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
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
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.shikiflow.R
import com.example.shikiflow.domain.model.sort.ReviewType
import com.example.shikiflow.domain.model.tracks.MediaType
import com.example.shikiflow.presentation.common.ErrorItem
import com.example.shikiflow.presentation.common.SortBottomSheet
import com.example.shikiflow.presentation.common.SortConfig
import com.example.shikiflow.presentation.screen.main.details.MediaNavOptions
import com.example.shikiflow.presentation.viewmodel.media.reviews.MediaReviewsViewModel

@Composable
fun MediaReviewsScreen(
    mediaId: Int,
    mediaType: MediaType,
    navOptions: MediaNavOptions,
    mediaReviewsViewModel: MediaReviewsViewModel = hiltViewModel()
) {
    val params by mediaReviewsViewModel.params.collectAsStateWithLifecycle()
    val mediaReviewItems = mediaReviewsViewModel.mediaReviews.collectAsLazyPagingItems()
    var showBottomSheet by remember { mutableStateOf(false) }

    LaunchedEffect(mediaId) {
        mediaReviewsViewModel.setData(mediaId, mediaType)
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showBottomSheet = true },
                containerColor = MaterialTheme.colorScheme.surfaceContainer,
                contentColor = MaterialTheme.colorScheme.onSurface
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_sort),
                    contentDescription = "Show Sort Bottom Sheet"
                )
            }
        }
    ) { paddingValues ->
        when(mediaReviewItems.loadState.refresh) {
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
                        onButtonClick = { mediaReviewItems.refresh() }
                    )
                }
            }
            else -> {
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(minSize = 264.dp),
                    contentPadding = PaddingValues(
                        start = 12.dp,
                        end = 12.dp,
                        top = paddingValues.calculateTopPadding(),
                        bottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
                    ),
                    horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.Start),
                    verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.Top),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(mediaReviewItems.itemCount) { index ->
                        mediaReviewItems[index]?.let { reviewShort ->
                            ReviewShortItem(
                                review = reviewShort,
                                onReviewClick = { reviewId ->
                                    navOptions.navigateToReview(reviewId)
                                }
                            )
                        }
                    }

                    mediaReviewItems.apply {
                        when {
                            loadState.append is LoadState.Error -> {
                                item {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 8.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        ErrorItem(
                                            message = stringResource(R.string.common_error),
                                            buttonLabel = stringResource(R.string.common_retry),
                                            onButtonClick = { mediaReviewItems.retry() }
                                        )
                                    }
                                }
                            }
                            loadState.append is LoadState.Loading -> {
                                item {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 8.dp),
                                        contentAlignment = Alignment.Center
                                    ) { CircularProgressIndicator() }
                                }
                            }
                        }
                    }
                }
            }
        }

        if(showBottomSheet) {
            SortBottomSheet(
                config = SortConfig(
                    options = ReviewType.entries,
                    selected = params.sort,
                    onSortChange = { reviewSort ->
                        mediaReviewsViewModel.setSort(reviewSort)
                    }
                ),
                onDismiss = { showBottomSheet = false }
            )
        }
    }
}