package com.example.shikiflow.presentation.screen.main

import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.example.shikiflow.domain.model.track.UserRateStatus
import com.example.shikiflow.R
import com.example.shikiflow.data.mapper.MediaTracksMapper.toUserRateData
import com.example.shikiflow.domain.model.track.manga.MangaTrack
import com.example.shikiflow.domain.model.tracks.RateUpdateState
import com.example.shikiflow.presentation.common.ErrorItem
import com.example.shikiflow.presentation.common.PullToRefreshCustomBox
import com.example.shikiflow.presentation.common.UserRateBottomSheet
import com.example.shikiflow.presentation.common.image.BaseImage
import com.example.shikiflow.presentation.common.image.ImageType
import com.example.shikiflow.presentation.common.mappers.MediaFormatMapper.displayValue
import com.example.shikiflow.presentation.viewmodel.manga.tracks.MangaTracksViewModel

@Composable
fun MangaTracksPage(
    userStatus: UserRateStatus,
    isAppBarVisible: Boolean,
    onMangaClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
    mangaTracksViewModel: MangaTracksViewModel = hiltViewModel()
) {
    val params by mangaTracksViewModel.params.collectAsStateWithLifecycle()
    val mangaTrackItems = mangaTracksViewModel.mangaTracks[userStatus]?.collectAsLazyPagingItems()
        ?: return

    var selectedItem by remember { mutableStateOf<MangaTrack?>(null) }

    when(mangaTrackItems.loadState.refresh) {
        LoadState.Loading -> {
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
                    message = stringResource(R.string.mtp_loading_error),
                    buttonLabel = stringResource(R.string.common_retry),
                    onButtonClick = { mangaTrackItems.refresh() }
                )
            }
        }
        else -> {
            PullToRefreshCustomBox(
                isRefreshing = mangaTrackItems.loadState.refresh is LoadState.Loading,
                enabled = isAppBarVisible,
                onRefresh = { mangaTrackItems.refresh() },
                modifier = Modifier.fillMaxSize()
            ) {
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(120.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.Top),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = modifier.fillMaxSize()
                ) {
                    items(
                        count = mangaTrackItems.itemCount,
                        key = mangaTrackItems.itemKey { it.track.id }
                    ) { index ->
                        val trackItem = mangaTrackItems[index] ?: return@items

                        MangaTrackItem(
                            trackItem = trackItem,
                            onItemClick = { id ->
                                onMangaClick(id)
                            },
                            onLongItemClick = { item ->
                                selectedItem = item
                            },
                            modifier = Modifier.animateItem()
                        )
                    }

                    if (mangaTrackItems.loadState.append is LoadState.Loading) {
                        item(span = { GridItemSpan(maxLineSpan) }) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) { CircularProgressIndicator() }
                        }
                    }
                    if (mangaTrackItems.loadState.append is LoadState.Error) {
                        item(span = { GridItemSpan(maxLineSpan) }) {
                            ErrorItem(
                                message = stringResource(R.string.mtp_loading_error),
                                buttonLabel = stringResource(R.string.common_retry),
                                showFace = false,
                                onButtonClick = { mangaTrackItems.retry() }
                            )
                        }
                    }
                }
            }
            selectedItem?.let {
                UserRateBottomSheet(
                    userRate = it.toUserRateData(),
                    rateUpdateState = params.rateUpdateState,
                    onDismiss = {
                        if (params.rateUpdateState != RateUpdateState.LOADING) {
                            selectedItem = null
                        }
                    },
                    onSave = { saveUserRate ->
                        mangaTracksViewModel.saveUserRate(saveUserRate)
                    }
                )
            }
        }
    }
}

@Composable
private fun MangaTrackItem(
    trackItem: MangaTrack,
    onItemClick: (Int) -> Unit,
    onLongItemClick: (MangaTrack) -> Unit,
    modifier: Modifier = Modifier
) {
    val cornerShape = 12.dp
    val textHorizontalPadding = 2.dp

    Column(
        modifier = modifier.clip(
            shape = RoundedCornerShape(
                topStart = cornerShape,
                topEnd = cornerShape,
                bottomStart = 4.dp,
                bottomEnd = 4.dp
            )
        )
            .combinedClickable(
                onClick = { onItemClick(trackItem.manga.id) },
                onLongClick = { onLongItemClick(trackItem) }
            ),
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        BaseImage(
            model = trackItem.manga.poster?.originalUrl,
            contentScale = ContentScale.Crop,
            imageType = ImageType.Poster(
                defaultWidth = Int.MAX_VALUE.dp,
                defaultClip = RoundedCornerShape(cornerShape)
            )
        )

        Text(
            text = trackItem.manga.name,
            style = MaterialTheme.typography.labelSmall,
            overflow = TextOverflow.Ellipsis,
            maxLines = 2,
            modifier = Modifier.padding(
                start = textHorizontalPadding,
                end = textHorizontalPadding,
                top = 4.dp
            )
        )

        Text(
            text = listOfNotNull(
                trackItem.manga.kind?.let {
                    stringResource(id = it.displayValue())
                },
                trackItem.manga.score?.let { score ->
                    stringResource(id = R.string.media_score, score)
                }
            ).joinToString(" • "),
            style = MaterialTheme.typography.labelSmall.copy(
                fontSize = 10.sp
            ),
            modifier = Modifier.padding(
                start = textHorizontalPadding,
                end = textHorizontalPadding,
                bottom = 2.dp
            )
        )
    }
}