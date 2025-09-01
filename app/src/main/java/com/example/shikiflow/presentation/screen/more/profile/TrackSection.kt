package com.example.shikiflow.presentation.screen.more.profile

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
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

    ConstraintLayout(
        modifier = modifier.fillMaxWidth()
    ) {
        val (titleRef, animeDataRef, mangaDataRef) = createRefs()

        Text(
            text = stringResource(R.string.profile_screen_track_lists_label),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.constrainAs(titleRef) {
                top.linkTo(parent.top)
                start.linkTo(parent.start)
            }
        )

        TrackItem(
            iconResource = IconResource.Drawable(R.drawable.ic_anime),
            type = stringResource(R.string.main_track_mode_anime),
            groupedData = groupedAnimeData,
            itemsCount = animeItemsCount,
            modifier = Modifier.constrainAs(animeDataRef) {
                top.linkTo(titleRef.bottom, margin = 12.dp)
                start.linkTo(parent.start)
            }
        )

        TrackItem(
            iconResource = IconResource.Drawable(R.drawable.ic_manga),
            type = stringResource(R.string.main_track_mode_manga),
            groupedData = groupedMangaData,
            itemsCount = mangaItemsCount,
            modifier = Modifier.constrainAs(mangaDataRef) {
                top.linkTo(animeDataRef.bottom, margin = 12.dp)
                start.linkTo(parent.start)
            }
        )
    }
}