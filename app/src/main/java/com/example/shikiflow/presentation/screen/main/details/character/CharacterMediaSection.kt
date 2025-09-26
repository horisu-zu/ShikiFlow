package com.example.shikiflow.presentation.screen.main.details.character

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.shikiflow.domain.model.anime.Browse
import com.example.shikiflow.domain.model.tracks.MediaType
import com.example.shikiflow.presentation.screen.browse.BrowseGridItem

@Composable
fun CharacterMediaSection(
    sectionTitle: String,
    items: List<Browse>,
    horizontalPadding: Dp = 12.dp,
    onItemClick: (String, MediaType) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = sectionTitle,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(horizontal = horizontalPadding)
        )
        LazyRow(
            contentPadding = PaddingValues(horizontal = horizontalPadding),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(items.size) { index ->
                val mediaItem = items[index]

                BrowseGridItem(
                    browseItem = mediaItem,
                    onItemClick = onItemClick,
                    modifier = Modifier.width(96.dp)
                )
            }
        }
    }
}