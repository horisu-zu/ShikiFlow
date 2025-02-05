package com.example.shikiflow.presentation.screen.main

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.graphql.AnimeTracksQuery
import com.example.graphql.type.UserRateStatusEnum
import com.example.shikiflow.data.tracks.toUiModel
import com.example.shikiflow.presentation.common.UserRateBottomSheet
import com.example.shikiflow.presentation.viewmodel.anime.AnimeTracksViewModel
import com.example.shikiflow.presentation.viewmodel.user.UserViewModel

@Composable
fun AnimeTracksPage(
    rootNavController: NavController,
    trackViewModel: AnimeTracksViewModel = hiltViewModel(),
    userViewModel: UserViewModel = hiltViewModel(),
    status: UserRateStatusEnum?
) {
    val userRates by trackViewModel.userRates.collectAsState()
    val isLoading by trackViewModel.isLoading.collectAsState()
    val hasMorePages by trackViewModel.hasMorePages.collectAsState()
    val shouldLoadMore = remember(status) {
        derivedStateOf {
            hasMorePages[status] == true && isLoading[status] != true
        }
    }
    var selectedItem by remember { mutableStateOf<AnimeTracksQuery.UserRate?>(null) }
    var rateBottomSheet by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        val items = userRates[status] ?: emptyList()

        items(items.size) { index ->
            val userRate = items[index]
            AnimeTrackItem(
                userRate,
                onClick = { id ->
                    rootNavController.navigate("animeDetailsScreen/$id")
                },
                onLongClick = {
                    rateBottomSheet = true
                    selectedItem = userRate
                }
            )

            if (index >= items.size - 5 && shouldLoadMore.value) {
                LaunchedEffect(status) {
                    status?.let { trackViewModel.loadAnimeTracks(it) }
                }
            }
        }

        if (isLoading[status] == true && (items.isEmpty() || shouldLoadMore.value)) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }
    }

    if (rateBottomSheet) {
        val isUpdating by userViewModel.isUpdating.collectAsState()

        selectedItem?.let {
            UserRateBottomSheet(
                userRate = it.toUiModel(),
                isLoading = isUpdating,
                onDismiss = { if (!isUpdating) rateBottomSheet = false },
                onSave = { id, rateStatus, score, episodes, rewatches, mediaType ->
                    userViewModel.updateUserRate(
                        id = id,
                        status = rateStatus,
                        score = score,
                        progress = episodes,
                        rewatches = rewatches,
                        mediaType = mediaType
                    ) { success ->
                        if (success) {
                            status?.let { currentStatus ->
                                trackViewModel.refreshAfterStatusUpdate(currentStatus, rateStatus)
                                rateBottomSheet = false
                            }
                        }
                    }
                }
            )
        }
    }
}