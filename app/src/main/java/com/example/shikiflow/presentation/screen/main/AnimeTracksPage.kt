package com.example.shikiflow.presentation.screen.main

import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.example.shikiflow.domain.model.track.UserRateStatus
import com.example.shikiflow.R
import com.example.shikiflow.data.mapper.local.TracksMapper.toUserRateData
import com.example.shikiflow.domain.model.tracks.RateUpdateState
import com.example.shikiflow.domain.model.media_details.MediaStatus
import com.example.shikiflow.presentation.common.ErrorItem
import com.example.shikiflow.presentation.common.PullToRefreshCustomBox
import com.example.shikiflow.presentation.common.UserRateBottomSheet
import com.example.shikiflow.presentation.common.image.BaseImage
import com.example.shikiflow.presentation.common.image.ImageType
import com.example.shikiflow.presentation.viewmodel.anime.tracks.AnimeTracksViewModel
import com.example.shikiflow.domain.model.settings.AppUiMode
import com.example.shikiflow.domain.model.track.media.MediaTrack

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnimeTracksPage(
    userStatus: UserRateStatus,
    isAppBarVisible: Boolean,
    onAnimeClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
    tracksViewModel: AnimeTracksViewModel = hiltViewModel(),
) {
    val params by tracksViewModel.params.collectAsStateWithLifecycle()
    val animeTrackItems = tracksViewModel.animeTracks[userStatus]?.collectAsLazyPagingItems()
        ?: return
    val appUiMode by tracksViewModel.appUiMode.collectAsStateWithLifecycle()

    var selectedItem by remember { mutableStateOf<MediaTrack?>(null) }

    when (animeTrackItems.loadState.refresh) {
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
                    message = stringResource(R.string.atp_loading_error),
                    buttonLabel = stringResource(R.string.common_retry),
                    onButtonClick = { animeTrackItems.refresh() }
                )
            }
        }
        else -> {
            PullToRefreshCustomBox(
                isRefreshing = animeTrackItems.loadState.refresh is LoadState.Loading,
                enabled = isAppBarVisible,
                onRefresh = { animeTrackItems.refresh() }
            ) {
                when (appUiMode) {
                    AppUiMode.LIST -> {
                        AnimeTracksListComponent(
                            trackItems = animeTrackItems,
                            onAnimeClick = onAnimeClick,
                            onLongClick = { item ->
                                selectedItem = item
                            },
                            modifier = modifier
                        )
                    }
                    AppUiMode.GRID -> {
                        AnimeTracksGridComponent(
                            trackItems = animeTrackItems,
                            onAnimeClick = onAnimeClick,
                            onLongClick = { item ->
                                selectedItem = item
                            },
                            modifier = modifier
                        )
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
                        tracksViewModel.saveUserRate(saveUserRate)
                    }
                )
            }
        }
    }
}

@Composable
private fun AnimeTracksListComponent(
    trackItems: LazyPagingItems<MediaTrack>,
    onAnimeClick: (Int) -> Unit,
    onLongClick: (MediaTrack) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            horizontal = 12.dp,
            vertical = 12.dp
        ),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(
            count = trackItems.itemCount,
            key = trackItems.itemKey { it.track.id }
        ) { index ->
            val item = trackItems[index] ?: return@items

            AnimeTrackItem(
                userRate = item,
                onClick = onAnimeClick,
                onLongClick = { onLongClick(item) },
                modifier = Modifier.animateItem()
            )
        }
        trackItems.apply {
            if(loadState.append is LoadState.Loading) {
                item {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) { CircularProgressIndicator() }
                }
            }
            if(loadState.append is LoadState.Error) {
                item {
                    ErrorItem(
                        message = stringResource(R.string.atp_loading_error),
                        buttonLabel = stringResource(R.string.common_retry),
                        showFace = false,
                        onButtonClick = { trackItems.retry() }
                    )
                }
            }
        }
    }
}

@Composable
private fun AnimeTracksGridComponent(
    trackItems: LazyPagingItems<MediaTrack>,
    onAnimeClick: (Int) -> Unit,
    onLongClick: (MediaTrack) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(120.dp),
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(
            count = trackItems.itemCount,
            key = trackItems.itemKey { it.track.id }
        ) { index ->
            val trackItem = trackItems[index] ?: return@items

            AnimeTrackGridItem(
                trackItem = trackItem,
                onClick = onAnimeClick,
                onLongClick = onLongClick,
                modifier = Modifier.animateItem()
            )
        }
        trackItems.apply {
            if(loadState.append is LoadState.Loading) {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) { CircularProgressIndicator() }
                }
            }
            if(loadState.append is LoadState.Error) {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    ErrorItem(
                        message = stringResource(R.string.atp_loading_error),
                        buttonLabel = stringResource(R.string.common_retry),
                        onButtonClick = { trackItems.retry() }
                    )
                }
            }
        }
    }
}

@Composable
fun AnimeTrackGridItem(
    trackItem: MediaTrack,
    onClick: (Int) -> Unit,
    onLongClick: (MediaTrack) -> Unit,
    modifier: Modifier = Modifier
) {
    val imageType = ImageType.Poster(
        width = Int.MAX_VALUE.dp,
        aspectRatio = 2f / 2.6f
    )

    Column(
        modifier = Modifier
            .clip(imageType.clip)
            .combinedClickable(
                onClick = { onClick(trackItem.shortData.id) },
                onLongClick = { onLongClick(trackItem) }
            )
            .then(modifier),
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        BaseImage(
            model = trackItem.shortData.poster?.originalUrl,
            imageType = imageType,
            contentDescription = "Poster"
        )
        Text(
            text = trackItem.shortData.name,
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.Medium
            ),
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(top = 2.dp)
        )
        Text(
            text = buildString {
                if(trackItem.shortData.status == MediaStatus.ONGOING)
                    append("${trackItem.track.progress} / ${trackItem.shortData.currentProgress}")
                else append(trackItem.track.progress)
                append(" of ${trackItem.shortData.totalCount.takeIf { (it ?: 0) > 0 } ?: "?"} ep.")
            }, style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Light,
                fontSize = 10.sp
            ),
            modifier = Modifier.padding(bottom = 4.dp)
        )
    }
}