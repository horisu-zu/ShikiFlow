package com.example.shikiflow.presentation.screen.main.details.character

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.shikiflow.data.anime.Browse
import com.example.shikiflow.data.tracks.MediaType
import com.example.shikiflow.presentation.screen.browse.BrowseItem

@Composable
fun CharacterMediaSection(
    sectionTitle: String,
    items: List<Browse>,
    onItemClick: (String, MediaType) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = sectionTitle,
            style = MaterialTheme.typography.titleMedium
        )
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(items.size) { index ->
                val mediaItem = items[index]

                BrowseItem(
                    browseItem = mediaItem,
                    onItemClick = onItemClick,
                    modifier = Modifier
                )
            }
        }
    }
}