package com.example.shikiflow.presentation.screen.main

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.itemKey
import com.example.shikiflow.data.local.entity.animetrack.AnimeTrack
import com.example.shikiflow.data.local.entity.animetrack.AnimeTrack.Companion.toUserRateData
import com.example.shikiflow.data.local.entity.animetrack.AnimeTrackEntity.Companion.toEntity
import com.example.shikiflow.presentation.screen.MainNavRoute
import com.example.shikiflow.presentation.common.UserRateBottomSheet
import com.example.shikiflow.presentation.viewmodel.anime.AnimeTracksViewModel
import com.example.shikiflow.presentation.viewmodel.user.UserViewModel

@Composable
fun AnimeTracksPage(
    trackItems: LazyPagingItems<AnimeTrack>,
    tracksViewModel: AnimeTracksViewModel,
    userViewModel: UserViewModel = hiltViewModel(),
    onAnimeClick: (String) -> Unit
) {
    var selectedItem by remember { mutableStateOf<AnimeTrack?>(null) }
    var rateBottomSheet = remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        userViewModel.updateEvent.collect { userRate ->
            tracksViewModel.updateAnimeTrack(userRate.toEntity())
            rateBottomSheet.value = false
        }
    }

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