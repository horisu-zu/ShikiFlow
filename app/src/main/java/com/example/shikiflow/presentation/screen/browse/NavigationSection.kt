package com.example.shikiflow.presentation.screen.browse

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.shikiflow.R
import com.example.shikiflow.domain.model.anime.BrowseType
import com.example.shikiflow.presentation.common.NavigationCard
import com.example.shikiflow.presentation.common.mappers.BrowseTypeMapper.displayValue
import com.example.shikiflow.utils.IconResource

@Composable
fun NavigationSection(
    modifier: Modifier = Modifier,
    onNavigateSideScreen: (BrowseType) -> Unit
) {
    FlowRow(
        maxItemsInEachRow = 2,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = modifier
    ) {
        val cardModifier = Modifier
            .weight(1f)
            .padding(horizontal = 12.dp, vertical = 16.dp)

        NavigationCard(
            icon = IconResource.Drawable(R.drawable.ic_anime) ,
            title = stringResource(BrowseType.AnimeBrowseType.ANIME_TOP.displayValue()),
            onClick = { onNavigateSideScreen(BrowseType.AnimeBrowseType.ANIME_TOP) },
            modifier = cardModifier
        )
        NavigationCard(
            icon = IconResource.Drawable(R.drawable.ic_manga) ,
            title = stringResource(BrowseType.MangaBrowseType.MANGA_TOP.displayValue()),
            onClick = { onNavigateSideScreen(BrowseType.MangaBrowseType.MANGA_TOP) },
            modifier = cardModifier
        )
        NavigationCard(
            icon = IconResource.Drawable(R.drawable.ic_trend_up) ,
            title = stringResource(BrowseType.AnimeBrowseType.ANIME_POPULARITY.displayValue()),
            onClick = { onNavigateSideScreen(BrowseType.AnimeBrowseType.ANIME_POPULARITY) },
            modifier = cardModifier
        )
        NavigationCard(
            icon = IconResource.Drawable(R.drawable.ic_fire) ,
            title = stringResource(BrowseType.MangaBrowseType.MANGA_POPULARITY.displayValue()),
            onClick = { onNavigateSideScreen(BrowseType.MangaBrowseType.MANGA_POPULARITY) },
            modifier = cardModifier
        )
        NavigationCard(
            icon = IconResource.Vector(Icons.Default.DateRange),
            title = stringResource(BrowseType.AnimeBrowseType.ONGOING.displayValue()),
            onClick = { onNavigateSideScreen(BrowseType.AnimeBrowseType.ONGOING) },
            modifier = cardModifier
        )
    }
}