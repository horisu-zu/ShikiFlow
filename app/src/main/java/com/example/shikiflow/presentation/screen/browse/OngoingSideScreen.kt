package com.example.shikiflow.presentation.screen.browse

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import com.example.shikiflow.data.anime.Browse
import com.example.shikiflow.presentation.screen.MainNavRoute
import com.example.shikiflow.utils.Converter.formatDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

@Composable
fun OngoingSideScreen(
    ongoingData: LazyPagingItems<Browse>,
    rootNavController: NavController,
    modifier: Modifier = Modifier
) {
    val groupedOngoings = remember(ongoingData.loadState) {
        ongoingData.itemSnapshotList.items
            .filter { it.nextEpisodeAt != null }
            .groupBy { item -> item.nextEpisodeAt!!
                .toLocalDateTime(TimeZone.currentSystemDefault()).date }
            .toSortedMap()
            .map { (date, values) ->
                formatDate(date) to values.sortedByDescending { it.score }
            }.toMap()
    }

    LazyColumn(
        modifier = modifier.fillMaxSize().padding(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        if(ongoingData.loadState.refresh is LoadState.Loading) {
            item {
                Box(
                    modifier = Modifier.fillParentMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }
        groupedOngoings.forEach { (date, animeValues) ->
            item {
                Text(
                    text = date.toString(),
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(horizontal = 12.dp)
                )
            }
            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(animeValues) { anime ->
                        BrowseItem(
                            browseItem = anime,
                            onItemClick = { id, mediaType ->
                                rootNavController.navigate(MainNavRoute.AnimeDetails(id))
                            },
                            modifier = Modifier.width(120.dp)
                        )
                    }
                }
            }
        }
        item {
            Text(
                text = "*Items are grouped by date and sorted by score.\nCurrently screen supports " +
                        "functionality for the first 45 rated ongoings (and among them only those " +
                        "with a release date of the next episode can be shown).",
                modifier = Modifier.fillMaxWidth().padding(bottom = 4.dp),
                style = MaterialTheme.typography.labelSmall.copy(
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                )
            )
        }
    }
}