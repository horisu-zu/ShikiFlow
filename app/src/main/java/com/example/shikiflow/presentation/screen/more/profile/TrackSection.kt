package com.example.shikiflow.presentation.screen.more.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.shikiflow.R
import com.example.shikiflow.domain.model.tracks.MediaType
import com.example.shikiflow.domain.model.tracks.TargetType
import com.example.shikiflow.domain.model.tracks.UserRate
import com.example.shikiflow.utils.Converter.groupAndSortByStatus
import com.example.shikiflow.utils.IconResource

@Composable
fun TrackSection(
    userRateData: List<UserRate>,
    modifier: Modifier = Modifier
) {
    val animeTrackData = userRateData.filter { it.targetType == TargetType.ANIME }
    val mangaTrackData = userRateData.filter { it.targetType == TargetType.MANGA }

    val animeItemsCount = animeTrackData.size
    val groupedAnimeData = animeTrackData.groupAndSortByStatus(MediaType.ANIME)

    val mangaItemsCount = mangaTrackData.size
    val groupedMangaData = mangaTrackData.groupAndSortByStatus(MediaType.MANGA)

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.Top)
    ) {
        Text(
            text = stringResource(R.string.profile_screen_track_lists_label),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        TrackItem(
            iconResource = IconResource.Drawable(R.drawable.ic_anime),
            type = stringResource(R.string.main_track_mode_anime),
            groupedData = groupedAnimeData,
            itemsCount = animeItemsCount
        )

        TrackItem(
            iconResource = IconResource.Drawable(R.drawable.ic_manga),
            type = stringResource(R.string.main_track_mode_manga),
            groupedData = groupedMangaData,
            itemsCount = mangaItemsCount
        )
    }
}