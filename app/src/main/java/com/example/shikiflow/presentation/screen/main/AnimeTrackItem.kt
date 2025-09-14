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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.graphql.type.AnimeStatusEnum
import com.example.shikiflow.R
import com.example.shikiflow.domain.model.mapper.UserRateMapper.Companion.determineSeason
import com.example.shikiflow.domain.model.mapper.UserRateMapper.Companion.mapAnimeKind
import com.example.shikiflow.domain.model.mapper.UserRateMapper.Companion.mapAnimeStatus
import com.example.shikiflow.domain.model.track.anime.AnimeTrack
import com.example.shikiflow.presentation.common.ProgressBar
import com.example.shikiflow.presentation.common.StatusCard
import com.example.shikiflow.presentation.common.image.BaseImage

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AnimeTrackItem(
    userRate: AnimeTrack,
    modifier: Modifier = Modifier,
    onClick: (String) -> Unit,
    onLongClick: () -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 12.dp, end = 16.dp)
            .clip(RoundedCornerShape(12.dp))
            .combinedClickable(
                onClick = { onClick(userRate.anime.id) },
                onLongClick = { onLongClick() }
            ),
        horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.Start)
    ) {
        BaseImage(
            model = userRate.anime.poster?.originalUrl,
            contentDescription = "Poster",
            modifier = Modifier.width(96.dp)
        )

        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp, Alignment.Top)
        ) {
            Text(
                text = userRate.anime.name,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                //fontSize = 10.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = buildString {
                    append(mapAnimeStatus(userRate.anime.status))
                    append(" • ")
                    append(mapAnimeKind(userRate.anime.kind))
                    append(" • ")
                    append(
                        stringResource(
                            id = R.string.episodes,
                            userRate.anime.episodes.takeIf { it > 0 } ?: "?"
                        )
                    )
                    if (userRate.anime.status != AnimeStatusEnum.anons) {
                        append(" • ")
                        append("${userRate.anime.score} ★")
                    }
                }, fontSize = 12.sp
            )

            if (userRate.anime.status != AnimeStatusEnum.anons) {
                Column {
                    ProgressBar(
                        progress = userRate.anime.let { animeShort ->
                            val totalEpisodes = when {
                                animeShort.episodes > 0 -> animeShort.episodes
                                animeShort.episodesAired > 0 -> animeShort.episodesAired
                                else -> null
                            }
                            totalEpisodes?.let { userRate.track.episodes.toFloat() / it } ?: 0f
                        }
                    )
                    Text(
                        text = stringResource(
                            id = R.string.ongoing_episodes,
                            userRate.track.episodes,
                            userRate.anime.let { animeShort ->
                                val totalEpisodes = when {
                                    animeShort.episodes > 0 -> animeShort.episodes
                                    animeShort.episodesAired > 0 -> animeShort.episodesAired
                                    else -> 0
                                }
                                totalEpisodes
                            }
                        ), fontSize = 12.sp
                    )
                }
            } else {
                StatusCard(text = determineSeason(userRate.anime.airedOn))
            }
        }
    }
}