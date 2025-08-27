package com.example.shikiflow.presentation.screen.main

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.itemKey
import com.example.shikiflow.R
import com.example.shikiflow.domain.model.track.anime.AnimeTrack
import com.example.shikiflow.domain.model.track.anime.AnimeTrack.Companion.toUserRateData
import com.example.shikiflow.domain.model.track.anime.AnimeUserTrack.Companion.toEntity
import com.example.shikiflow.presentation.common.ErrorItem
import com.example.shikiflow.presentation.common.UserRateBottomSheet
import com.example.shikiflow.presentation.viewmodel.anime.AnimeTracksViewModel
import com.example.shikiflow.presentation.viewmodel.user.UserViewModel

@Composable
fun AnimeTracksPage(
    trackItems: LazyPagingItems<AnimeTrack>?,
    tracksViewModel: AnimeTracksViewModel,
    userViewModel: UserViewModel = hiltViewModel(),
    onAnimeClick: (String) -> Unit
) {
    var selectedItem by remember { mutableStateOf<AnimeTrack?>(null) }
    val rateBottomSheet = remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        userViewModel.updateEvent.collect { userRate ->
            tracksViewModel.updateAnimeTrack(userRate.toEntity())
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
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
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
                        onClick = { id -> onAnimeClick(id) },
                        onLongClick = {
                            rateBottomSheet.value = true
                            selectedItem = item
                        }
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
                }
            }

            if (rateBottomSheet.value) {
                val isUpdating by userViewModel.isUpdating.collectAsState()

                selectedItem?.let {
                    UserRateBottomSheet(
                        userRate = it.toUserRateData(),
                        isLoading = isUpdating,
                        onDismiss = { if (!isUpdating) rateBottomSheet.value = false },
                        onSave = { id, rateStatus, score, episodes, rewatches, mediaType ->
                            userViewModel.updateUserRate(
                                id = id,
                                status = rateStatus,
                                score = score,
                                progress = episodes,
                                rewatches = rewatches,
                                mediaType = mediaType
                            )
                        }
                    )
                }
            }
        }
    }
}