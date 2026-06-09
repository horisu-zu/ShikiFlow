package com.example.shikiflow.presentation.screen.main.details.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.shikiflow.R
import com.example.shikiflow.domain.model.tracks.MediaType
import com.example.shikiflow.presentation.common.ErrorItem
import com.example.shikiflow.presentation.screen.main.details.MediaNavOptions
import com.example.shikiflow.presentation.screen.browse.BrowseGridItem
import com.example.shikiflow.presentation.screen.browse.BrowseGridItemPlaceholder
import com.example.shikiflow.presentation.screen.main.LocalTitleTypeController
import com.example.shikiflow.presentation.viewmodel.media.similar.SimilarMediaViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SimilarMediaScreen(
    mediaTitle: String,
    mediaId: Int,
    mediaType: MediaType,
    navOptions: MediaNavOptions,
    similarMediaViewModel: SimilarMediaViewModel = hiltViewModel()
) {
    val preferredTitleType = LocalTitleTypeController.current
    val lazyGridState = rememberLazyGridState()
    val similarMediaState = similarMediaViewModel.similarMediaFlow.collectAsLazyPagingItems()

    val horizontalPadding = 12.dp

    LaunchedEffect(mediaId) {
        similarMediaViewModel.setMediaParams(mediaId, mediaType)
    }

    when (similarMediaState.loadState.refresh) {
        is LoadState.Error -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                ErrorItem(
                    message = (similarMediaState.loadState.refresh as LoadState.Error)
                        .error.message ?: stringResource(R.string.similar_media_error),
                    buttonLabel = stringResource(R.string.common_retry),
                    onButtonClick = { similarMediaState.retry() }
                )
            }
        }
        else -> {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(96.dp),
                state = lazyGridState,
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = horizontalPadding, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                similarMediaState.apply {
                    stickyHeader {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterVertically),
                            modifier = Modifier
                                .background(MaterialTheme.colorScheme.surfaceContainerLow)
                                .padding(vertical = 8.dp)
                        ) {
                            Text(
                                text = stringResource(R.string.similar_media_app_bar_label),
                                style = MaterialTheme.typography.bodyLarge
                            )
                            Text(
                                text = mediaTitle,
                                style = MaterialTheme.typography.labelMedium.copy(
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f)
                                ),
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                    if (loadState.refresh is LoadState.Loading) {
                        items(count = 24) {
                            BrowseGridItemPlaceholder()
                        }
                    } else if (loadState.refresh is LoadState.NotLoading) {
                        items(
                            count = similarMediaState.itemCount,
                            key = { index -> similarMediaState[index]?.id ?: index }
                        ) { index ->
                            similarMediaState[index]?.let { mediaRecommendation ->
                                BrowseGridItem(
                                    browseItem = mediaRecommendation,
                                    titleType = preferredTitleType,
                                    onItemClick = { id, mediaType ->
                                        when (mediaType) {
                                            MediaType.ANIME -> navOptions.navigateToAnimeDetails(id)
                                            MediaType.MANGA -> navOptions.navigateToMangaDetails(id)
                                        }
                                    }
                                )
                            }
                        }
                    }

                    if (loadState.append is LoadState.Loading) {
                        item(span = { GridItemSpan(maxLineSpan) }) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) { CircularProgressIndicator() }
                        }
                    }
                    if (loadState.append is LoadState.Error) {
                        item(span = { GridItemSpan(maxLineSpan) }) {
                            ErrorItem(
                                message = stringResource(R.string.common_error),
                                showFace = false,
                                buttonLabel = stringResource(R.string.common_retry),
                                onButtonClick = { similarMediaState.retry() }
                            )
                        }
                    }
                }
            }
        }
    }
}