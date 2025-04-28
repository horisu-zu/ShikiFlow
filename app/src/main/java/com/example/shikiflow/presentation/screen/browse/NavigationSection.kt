package com.example.shikiflow.presentation.screen.browse

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.shikiflow.R
import com.example.shikiflow.data.anime.BrowseType
import com.example.shikiflow.presentation.common.NavigationCard
import com.example.shikiflow.utils.IconResource

@Composable
fun NavigationSection(
    modifier: Modifier = Modifier,
    onNavigateSideScreen: (BrowseType) -> Unit
) {
    Column (
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            NavigationCard(
                icon = IconResource.Drawable(R.drawable.ic_anime) ,
                title = "Anime's Top",
                onClick = { onNavigateSideScreen(BrowseType.AnimeBrowseType.ANIME_TOP) },
                modifier = Modifier.weight(1f),
            )
            NavigationCard(
                icon = IconResource.Drawable(R.drawable.ic_manga) ,
                title = "Manga's Top",
                onClick = { onNavigateSideScreen(BrowseType.MangaBrowseType.MANGA_TOP) },
                modifier = Modifier.weight(1f),
            )
        }
        NavigationCard(
            icon = IconResource.Vector(Icons.Default.DateRange),
            title = "Ongoings Calendar",
            onClick = {
                onNavigateSideScreen(BrowseType.AnimeBrowseType.ONGOING)
            }
        )
    }
}