package com.example.shikiflow.presentation.screen.browse

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.shikiflow.data.anime.BrowseState
import com.example.shikiflow.data.anime.toBrowseAnime
import com.example.shikiflow.utils.Converter.formatInstant
import kotlinx.datetime.Instant

@Composable
fun OngoingSideScreen(
    state: BrowseState.AnimeBrowseState,
    rootNavController: NavController,
    modifier: Modifier = Modifier
) {
    val groupedAnime = remember(state.items) {
        state.items
            .filter { it.nextEpisodeAt != null }
            .groupBy { anime ->
                formatInstant(Instant.parse(anime.nextEpisodeAt as String))
            }
            .toSortedMap(naturalOrder())
            .mapValues { (_, animeValues) ->
                animeValues.sortedByDescending { it.score ?: 0.0 }
            }
    }

    LazyColumn(
        modifier = modifier.fillMaxSize().padding(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        groupedAnime.forEach { (date, animeValues) ->
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
                            browseItem = anime.toBrowseAnime(),
                            onItemClick = { id ->
                                rootNavController.navigate("animeDetailsScreen/$id")
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