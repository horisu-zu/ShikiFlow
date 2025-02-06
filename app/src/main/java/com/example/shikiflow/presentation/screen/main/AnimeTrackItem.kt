package com.example.shikiflow.presentation.screen.main

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.example.graphql.AnimeTracksQuery
import com.example.graphql.type.AnimeStatusEnum
import com.example.shikiflow.data.mapper.UserRateMapper.Companion.determineSeason
import com.example.shikiflow.data.mapper.UserRateMapper.Companion.mapAnimeKind
import com.example.shikiflow.data.mapper.UserRateMapper.Companion.mapAnimeStatus
import com.example.shikiflow.presentation.common.ProgressBar
import com.example.shikiflow.presentation.common.StatusCard
import com.example.shikiflow.presentation.common.image.BaseImage

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AnimeTrackItem(
    userRate: AnimeTracksQuery.UserRate,
    modifier: Modifier = Modifier,
    onClick: (String) -> Unit,
    onLongClick: () -> Unit
) {
    ConstraintLayout(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 12.dp, end = 24.dp)
            .clip(RoundedCornerShape(12.dp))
            .combinedClickable(
                onClick = { onClick(userRate.animeUserRateWithModel.anime?.animeShort?.id ?: "") },
                onLongClick = { onLongClick() }
            )
    ) {
        val (posterRef, titleRef, dataRef, progressBarRef) = createRefs()

        BaseImage(
            model = userRate.animeUserRateWithModel.anime?.animeShort?.poster?.posterShort?.originalUrl,
            contentDescription = "Poster",
            modifier = Modifier
                .width(96.dp)
                .constrainAs(posterRef) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                }
        )

        Text(
            text = userRate.animeUserRateWithModel.anime?.animeShort?.name ?: "Huh!",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            //fontSize = 10.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.constrainAs(titleRef) {
                top.linkTo(parent.top)
                start.linkTo(posterRef.end, margin = 16.dp)
                end.linkTo(parent.end, margin = 16.dp)
                width = Dimension.fillToConstraints
            }
        )

        Row(
            modifier = Modifier.constrainAs(dataRef) {
                top.linkTo(titleRef.bottom, margin = 4.dp)
                start.linkTo(titleRef.start)
                end.linkTo(titleRef.end)
                width = Dimension.fillToConstraints
            },
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = mapAnimeStatus(userRate.animeUserRateWithModel.anime?.animeShort?.status),
                fontSize = 12.sp
            )
            Text(
                text = "•",
                fontSize = 12.sp
            )
            Text(
                text = mapAnimeKind(userRate.animeUserRateWithModel.anime?.animeShort?.kind),
                fontSize = 12.sp
            )
            Text(
                text = "•",
                fontSize = 12.sp
            )
            Text(
                text = "${userRate.animeUserRateWithModel.anime?.animeShort?.episodes?.takeIf { it > 0 } ?: "?"} ep.",
                fontSize = 12.sp
            )
            if (userRate.animeUserRateWithModel.anime?.animeShort?.status != AnimeStatusEnum.anons) {
                Text(
                    text = "•",
                    fontSize = 12.sp
                )
                Text(
                    text = "${userRate.animeUserRateWithModel.anime?.animeShort?.score} ★",
                    fontSize = 12.sp
                )
            }
        }

        if (userRate.animeUserRateWithModel.anime?.animeShort?.status != AnimeStatusEnum.anons) {
            Column(
                modifier = Modifier.constrainAs(progressBarRef) {
                    top.linkTo(dataRef.bottom, margin = 4.dp)
                    start.linkTo(dataRef.start)
                    end.linkTo(dataRef.end)
                }
            ) {
                ProgressBar(
                    progress = userRate.animeUserRateWithModel.anime?.animeShort?.let { animeShort ->
                        val totalEpisodes = when {
                            animeShort.episodes > 0 -> animeShort.episodes
                            animeShort.episodesAired > 0 -> animeShort.episodesAired
                            else -> null
                        }
                        totalEpisodes?.let { userRate.animeUserRateWithModel.episodes.toFloat() / it } ?: 0f
                    } ?: 0f
                )
                Text(
                    text = "${userRate.animeUserRateWithModel.episodes} of " +
                            "${userRate.animeUserRateWithModel.anime?.animeShort?.let { animeShort ->
                                val totalEpisodes = when {
                                    animeShort.episodes > 0 -> animeShort.episodes
                                    animeShort.episodesAired > 0 -> animeShort.episodesAired
                                    else -> 0
                                }
                                totalEpisodes
                            } ?: 0} ep.",
                    fontSize = 12.sp
                )
            }

        } else {
            StatusCard(
                text = determineSeason(userRate.animeUserRateWithModel.anime.animeShort.airedOn),
                modifier = Modifier.constrainAs(progressBarRef) {
                    top.linkTo(dataRef.bottom, margin = 4.dp)
                    start.linkTo(dataRef.start)
                }
            )
        }
    }
}