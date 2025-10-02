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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.itemKey
import com.example.graphql.type.AnimeStatusEnum
import com.example.shikiflow.R
import com.example.shikiflow.domain.model.track.anime.AnimeTrack
import com.example.shikiflow.domain.model.track.anime.AnimeTrack.Companion.toUserRateData
import com.example.shikiflow.presentation.common.ErrorItem
import com.example.shikiflow.presentation.common.UserRateBottomSheet
import com.example.shikiflow.presentation.common.image.BaseImage
import com.example.shikiflow.presentation.common.image.ImageType
import com.example.shikiflow.presentation.viewmodel.anime.AnimeTracksViewModel
import com.example.shikiflow.utils.AppUiMode

@Composable
fun AnimeTracksPage(
    trackItems: LazyPagingItems<AnimeTrack>?,
    tracksViewModel: AnimeTracksViewModel,
    onAnimeClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val appUiMode by tracksViewModel.appUiMode.collectAsStateWithLifecycle()
    var selectedItem by remember { mutableStateOf<AnimeTrack?>(null) }
    val rateBottomSheet = remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        tracksViewModel.updateEvent.collect { userRate ->
            rateBottomSheet.value = false
        }
    }

    trackItems?.let {
        if(trackItems.loadState.refresh is LoadState.Loading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) { CircularProgressIndicator() }
        } else if(trackItems.loadState.refresh is LoadState.Error) {
            ErrorItem(
                message = stringResource(R.string.atp_loading_error),
                buttonLabel = stringResource(R.string.common_retry),
                onButtonClick = { trackItems.refresh() }
            )
        } else {
            when(appUiMode) {
                AppUiMode.LIST -> {
                    AnimeTracksListComponent(
                        trackItems = trackItems,
                        onAnimeClick = onAnimeClick,
                        onLongClick = { item ->
                            rateBottomSheet.value = true
                            selectedItem = item
                        }, modifier = modifier
                    )
                }
                AppUiMode.GRID -> {
                    AnimeTracksGridComponent(
                        trackItems = trackItems,
                        onAnimeClick = onAnimeClick,
                        onLongClick = { item ->
                            rateBottomSheet.value = true
                            selectedItem = item
                        }, modifier = modifier
                    )
                }
            }

            if (rateBottomSheet.value) {
                val isUpdating by tracksViewModel.isUpdating.collectAsState()

                selectedItem?.let {
                    UserRateBottomSheet(
                        userRate = it.toUserRateData(),
                        isLoading = isUpdating,
                        onDismiss = { if (!isUpdating) rateBottomSheet.value = false },
                        onSave = { id, rateStatus, score, episodes, rewatches ->
                            tracksViewModel.updateUserRate(
                                id = id,
                                status = rateStatus,
                                score = score,
                                progress = episodes,
                                rewatches = rewatches
                            )
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun AnimeTracksListComponent(
    trackItems: LazyPagingItems<AnimeTrack>,
    onAnimeClick: (String) -> Unit,
    onLongClick: (AnimeTrack) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = 12.dp),
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
                onLongClick = { onLongClick(item) }
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
    trackItems: LazyPagingItems<AnimeTrack>,
    onAnimeClick: (String) -> Unit,
    onLongClick: (AnimeTrack) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
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

            Column(
                modifier = Modifier.clip(RoundedCornerShape(12.dp))
                    .combinedClickable(
                        onClick = { onAnimeClick(trackItem.anime.id) },
                        onLongClick = { onLongClick(trackItem) }
                    ),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                BaseImage(
                    model = trackItem.anime.poster?.originalUrl,
                    imageType = ImageType.Poster(
                        defaultWidth = Int.MAX_VALUE.dp,
                        defaultAspectRatio = 2f / 2.6f
                    ),
                    contentDescription = "Poster"
                )
                Text(
                    text = trackItem.anime.name,
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Medium
                    ),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(top = 2.dp)
                )
                Text(
                    text = buildString {
                        if(trackItem.anime.status == AnimeStatusEnum.ongoing)
                            append("${trackItem.track.episodes} / ${trackItem.anime.episodesAired}")
                        else append(trackItem.track.episodes)
                        append(" of ${trackItem.anime.episodes.takeIf { it > 0 } ?: "?"} ep.")
                    }, style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Light,
                        fontSize = 10.sp
                    ),
                    modifier = Modifier.padding(bottom = 4.dp)
                )
            }
        }
        trackItems.apply {
            if(loadState.append is LoadState.Loading) {
                item(span = { GridItemSpan(3) }) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) { CircularProgressIndicator() }
                }
            }
            if(loadState.append is LoadState.Error) {
                item(span = { GridItemSpan(3) }) {
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