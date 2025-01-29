package com.example.shikiflow.presentation.screen.more.profile

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import com.example.shikiflow.R
import com.example.shikiflow.data.user.TargetType
import com.example.shikiflow.data.user.UserRate
import com.example.shikiflow.data.user.UserRateContentType
import com.example.shikiflow.presentation.common.SegmentedProgressBar
import com.example.shikiflow.presentation.common.TypeItem
import com.example.shikiflow.utils.Converter.groupAndSortByStatus
import com.example.shikiflow.utils.IconResource

@Composable
fun TrackSection(
    userRateData: List<UserRate?>,
    modifier: Modifier = Modifier
) {
    val animeTrackData = userRateData.filter { it?.targetType == TargetType.ANIME }
    val mangaTrackData = userRateData.filter { it?.targetType == TargetType.MANGA }

    val animeItemsCount = animeTrackData.size
    val groupedAnimeData = animeTrackData.groupAndSortByStatus(UserRateContentType.ANIME) { it?.status }

    val mangaItemsCount = mangaTrackData.size
    val groupedMangaData = mangaTrackData.groupAndSortByStatus(UserRateContentType.MANGA) { it?.status }

    ConstraintLayout(
        modifier = modifier.fillMaxWidth()
    ) {
        val (titleRef, animeDataRef, animeProgressRef, mangaDataRef, mangaProgressRef) = createRefs()

        Text(
            text = "Lists",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.constrainAs(titleRef) {
                top.linkTo(parent.top)
                start.linkTo(parent.start)
            }
        )

        TypeItem(
            icon = IconResource.Drawable(R.drawable.ic_anime),
            type = "Anime",
            count = animeItemsCount.toString(),
            modifier = Modifier.constrainAs(animeDataRef) {
                top.linkTo(titleRef.bottom, margin = 6.dp)
                start.linkTo(parent.start)
            }
        )

        SegmentedProgressBar(
            groupedData = groupedAnimeData,
            totalCount = animeItemsCount,
            modifier = Modifier.constrainAs(animeProgressRef) {
                top.linkTo(animeDataRef.bottom, margin = 12.dp)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            }
        )

        TypeItem(
            icon = IconResource.Drawable(R.drawable.ic_manga),
            type = "Manga & Ranobe",
            count = mangaItemsCount.toString(),
            modifier = Modifier.constrainAs(mangaDataRef) {
                top.linkTo(animeProgressRef.bottom, margin = 12.dp)
                start.linkTo(parent.start)
            }
        )

        SegmentedProgressBar(
            groupedData = groupedMangaData,
            totalCount = mangaItemsCount,
            modifier = Modifier.constrainAs(mangaProgressRef) {
                top.linkTo(mangaDataRef.bottom, margin = 12.dp)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            }
        )
    }
}