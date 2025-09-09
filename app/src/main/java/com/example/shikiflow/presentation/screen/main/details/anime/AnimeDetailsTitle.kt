package com.example.shikiflow.presentation.screen.main.details.anime

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.outlined.Clear
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import com.example.graphql.AnimeDetailsQuery
import com.example.graphql.type.AnimeStatusEnum
import com.example.graphql.type.UserRateStatusEnum
import com.example.shikiflow.domain.model.mapper.UserRateMapper
import com.example.shikiflow.domain.model.mapper.UserRateMapper.Companion.mapAnimeKind
import com.example.shikiflow.domain.model.mapper.UserRateMapper.Companion.mapAnimeStatus
import com.example.shikiflow.domain.model.tracks.RateStatus
import com.example.shikiflow.presentation.common.StarScore
import com.example.shikiflow.presentation.common.image.GradientImage
import com.example.shikiflow.presentation.common.image.ImageType
import com.example.shikiflow.utils.Converter
import com.example.shikiflow.utils.IconResource
import com.example.shikiflow.utils.Resource
import com.example.shikiflow.utils.toIcon

@Composable
fun AnimeDetailsTitle(
    animeDetails: AnimeDetailsQuery.Anime,
    onStatusClick: () -> Unit,
    onPlayClick: (String, String, Int) -> Unit,
    modifier: Modifier = Modifier
) {
    ConstraintLayout(
        modifier = modifier.fillMaxWidth()
    ) {
        val (backgroundRef, scoreRef, titleRef, infoRow, statusItem) = createRefs()

        GradientImage(
            model = animeDetails.poster?.originalUrl,
            gradientFraction = 0.9f,
            imageType = ImageType.Poster(
                defaultClip = RoundedCornerShape(0.dp)
            ),
            modifier = Modifier
                .constrainAs(backgroundRef) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    bottom.linkTo(parent.bottom)
                }
        )

        if (animeDetails.status != AnimeStatusEnum.anons) {
            ScoreItem(
                score = animeDetails.score?.toFloat() ?: 0f,
                modifier = Modifier
                    .constrainAs(scoreRef) {
                        bottom.linkTo(titleRef.top)
                        start.linkTo(parent.start)
                    }.padding(horizontal = 12.dp)
            )
        }

        Text(
            text = animeDetails.name,
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier
                .constrainAs(titleRef) {
                    bottom.linkTo(infoRow.top, margin = 4.dp)
                    start.linkTo(parent.start)
                }.padding(horizontal = 12.dp)
        )

        Row(
            modifier = Modifier
                .constrainAs(infoRow) {
                    bottom.linkTo(statusItem.top, margin = 4.dp)
                    start.linkTo(parent.start)
                }.padding(horizontal = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ShortInfoItem(
                infoType = "Type",
                infoItem = "${mapAnimeKind(animeDetails.kind)} ∙ ${mapAnimeStatus(animeDetails.status)}"
            )
            ShortInfoItem(
                infoType = "Episodes",
                infoItem = if (animeDetails.status != AnimeStatusEnum.ongoing) {
                    "${animeDetails.episodes.takeIf { it != 0 } ?: "?"} ep."
                } else {
                    "${animeDetails.episodesAired} of ${animeDetails.episodes.takeIf { it != 0 } ?: "?"} ep."
                }
            )
            animeDetails.rating?.let { ratingEnum ->
                ShortInfoItem(
                    infoType = "Age Rating",
                    infoItem = Converter.convertRatingToString(ratingEnum)
                )
            }
        }

        UserStatusItem(
            onStatusClick = onStatusClick,
            onPlayClick = { onPlayClick(
                animeDetails.name,
                animeDetails.id,
                animeDetails.userRate?.episodes ?: 0
            ) },
            status = animeDetails.userRate?.status,
            allEpisodes = if(animeDetails.status == AnimeStatusEnum.released) animeDetails.episodes
                else animeDetails.episodesAired,
            watchedEpisodes = animeDetails.userRate?.episodes,
            score = animeDetails.userRate?.score,
            modifier = Modifier
                .constrainAs(statusItem) {
                    bottom.linkTo(parent.bottom)
                    start.linkTo(parent.start)
                }.padding(horizontal = 12.dp)
        )
    }
}

@Composable
fun ScoreItem(
    score: Float,
    modifier: Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        StarScore(score = score)
        Text(
            text = score.toString(),
            fontSize = 14.sp
        )
    }
}

@Composable
fun ShortInfoItem(
    infoType: String,
    infoItem: String
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Text(
            text = infoType,
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.labelMedium
        )
        Text(
            text = infoItem,
            style = MaterialTheme.typography.labelMedium
        )
    }
}

@Composable
private fun UserStatusItem(
    status: UserRateStatusEnum?,
    allEpisodes: Int?,
    watchedEpisodes: Int?,
    score: Int?,
    onStatusClick: () -> Unit,
    onPlayClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(12.dp))
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary,
                            MaterialTheme.colorScheme.tertiary
                        )
                    )
                )
                .clickable { onStatusClick() }
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.Start),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val icon = RateStatus.fromStatus(status ?: UserRateStatusEnum.UNKNOWN__)?.icon
                ?: IconResource.Vector(Icons.Outlined.Clear)

            icon.toIcon(
                modifier = Modifier.size(24.dp),
                tint = MaterialTheme.colorScheme.surface
            )
            Text(
                text = UserRateMapper.mapStatusToString(
                    status = status ?: UserRateStatusEnum.UNKNOWN__,
                    allEpisodes = allEpisodes,
                    watchedEpisodes = watchedEpisodes,
                    score = score
                ),
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.surface,
                    fontWeight = FontWeight.SemiBold
                )
            )
        }
        Box(
            modifier = Modifier.clip(RoundedCornerShape(12.dp))
                .clickable { onPlayClick() }
                .background(MaterialTheme.colorScheme.primaryContainer)
                .padding(12.dp)
        ) {
            Icon(
                imageVector = Icons.Default.PlayArrow,
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}