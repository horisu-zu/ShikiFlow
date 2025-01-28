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
import com.example.shikiflow.data.anime.ShortAnimeRate
import com.example.shikiflow.presentation.common.SegmentedProgressBar
import com.example.shikiflow.presentation.common.TypeItem
import com.example.shikiflow.utils.IconResource

@Composable
fun TrackSection(
    animeTrackData: List<ShortAnimeRate?>,
    modifier: Modifier = Modifier
) {
    val groupedData = animeTrackData
        .groupBy { it?.status }
        .mapValues { it.value.size }
    val itemsCount = animeTrackData.size

    ConstraintLayout(
        modifier = modifier.fillMaxWidth()
    ) {
        val (titleRef, typeRef, progressBarRef, statusRow) = createRefs()

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
            count = itemsCount.toString(),
            modifier = Modifier.constrainAs(typeRef) {
                top.linkTo(titleRef.bottom, margin = 4.dp)
                start.linkTo(parent.start)
            }
        )

        SegmentedProgressBar(
            groupedData = groupedData,
            totalCount = itemsCount,
            modifier = Modifier.constrainAs(progressBarRef) {
                top.linkTo(typeRef.bottom, margin = 12.dp)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            }
        )
    }
}