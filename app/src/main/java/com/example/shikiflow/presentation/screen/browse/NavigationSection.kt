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
import androidx.navigation.NavController
import com.example.shikiflow.R
import com.example.shikiflow.data.anime.BrowseType
import com.example.shikiflow.data.anime.navigateToSideScreen
import com.example.shikiflow.presentation.common.NavigationCard
import com.example.shikiflow.utils.IconResource

@Composable
fun NavigationSection(
    modifier: Modifier = Modifier,
    browseNavController: NavController
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
                onClick = {
                    browseNavController.navigateToSideScreen(BrowseType.AnimeBrowseType.ANIME_TOP)
                },
                modifier = Modifier.weight(1f),
            )
            NavigationCard(
                icon = IconResource.Drawable(R.drawable.ic_manga) ,
                title = "Manga's Top",
                onClick = { browseNavController.navigateToSideScreen(BrowseType.MangaBrowseType.MANGA_TOP) },
                modifier = Modifier.weight(1f),
            )
        }
        NavigationCard(
            icon = IconResource.Vector(Icons.Default.DateRange),
            title = "Ongoings Calendar",
            onClick = {
                browseNavController.navigateToSideScreen(BrowseType.AnimeBrowseType.ONGOING)
            }
        )
    }
}