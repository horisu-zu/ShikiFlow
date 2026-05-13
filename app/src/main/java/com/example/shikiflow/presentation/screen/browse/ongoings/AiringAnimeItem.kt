package com.example.shikiflow.presentation.screen.browse.ongoings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.example.shikiflow.R
import com.example.shikiflow.domain.model.anime.AiringAnime
import com.example.shikiflow.domain.model.media_details.MediaTitle.Companion.preferred
import com.example.shikiflow.domain.model.media_details.PreferredTitleType
import com.example.shikiflow.domain.model.tracks.MediaType
import com.example.shikiflow.presentation.common.BrowseCoverItem
import com.example.shikiflow.presentation.common.FadeEdge
import com.example.shikiflow.presentation.common.PulseIndicator
import com.example.shikiflow.presentation.common.VerticalGradientDivider
import com.example.shikiflow.presentation.common.mappers.ColorMapper.lerp
import com.example.shikiflow.presentation.common.mappers.ListActivityMapper.withStyledDigits
import com.example.shikiflow.presentation.screen.browse.ongoings.AiringStatus.Companion.color
import com.example.shikiflow.presentation.screen.browse.ongoings.AiringStatus.Companion.status
import com.materialkolor.ktx.harmonize
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Duration.Companion.minutes

@Composable
fun AiringAnimeItem(
    airingAnime: AiringAnime,
    titleType: PreferredTitleType,
    prevStatus: AiringStatus?,
    nextStatus: AiringStatus?,
    onClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val coverWidth = 96.dp
    val clip = 12.dp
    val dividerWidth = 4.dp
    val airingStatus = airingAnime.airingAt.status(
        duration = airingAnime.data.duration ?: 30.minutes
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(coverWidth * 3f / 2f),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.Start)
    ) {
        Column(
            modifier = Modifier.fillMaxHeight(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val background = MaterialTheme.colorScheme.background

            val color = airingStatus.color().harmonize(background)
            val backgroundColor = color.lerp(background)
            val prevColor = prevStatus?.color()?.harmonize(background)?.lerp(background)
            val nextColor = nextStatus?.color()?.harmonize(background)?.lerp(background)

            VerticalGradientDivider(
                thickness = dividerWidth,
                colors = prevColor?.let {
                    listOf(backgroundColor.lerp(prevColor, 0.5f), backgroundColor)
                },
                fadeEdge = FadeEdge.TOP,
                modifier = Modifier
                    .weight(1f)
                    .zIndex(-1f)
            )

            AiringStatusIcon(
                airingStatus = airingStatus,
                itemColor = color,
                backgroundColor = backgroundColor
            )

            VerticalGradientDivider(
                thickness = dividerWidth,
                colors = nextColor?.let {
                    listOf(backgroundColor, nextColor.lerp(backgroundColor, 0.5f))
                },
                fadeEdge = FadeEdge.BOTTOM,
                modifier = Modifier
                    .weight(3f)
                    .zIndex(-1f)
            )
        }

        Row(
            modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(clip))
                .clickable { onClick(airingAnime.data.id) }
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                verticalArrangement = Arrangement.spacedBy(2.dp, Alignment.Top)
            ) {
                Text(
                    text = buildAnnotatedString {
                        airingAnime.airingAt?.let { airingAt ->
                            val time = airingAt.toLocalDateTime(TimeZone.currentSystemDefault()).time

                            withStyle(
                                style = SpanStyle(
                                    fontWeight = FontWeight.SemiBold
                                )
                            ) {
                                append(time.toString())
                            }
                        } ?: airingAnime.releasedOn?.let {
                            stringResource(R.string.airing_released)
                        }
                    },
                    style = MaterialTheme.typography.titleSmall.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )

                Text(
                    text = airingAnime.data.title.preferred(titleType),
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.SemiBold
                    ),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = when(airingAnime.data.totalEpisodes) {
                        null -> stringResource(R.string.airing_episode, airingAnime.episode)
                        else -> stringResource(R.string.airing_episodes, airingAnime.episode, airingAnime.data.totalEpisodes)
                    }.withStyledDigits(
                        style = SpanStyle(
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    ),
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }

            BrowseCoverItem(
                posterUrl = airingAnime.data.coverImageUrl,
                mediaType = MediaType.ANIME,
                userRateStatus = airingAnime.data.userRateStatus,
                coverWidth = coverWidth,
                cornerShape = clip
            )
        }
    }
}



@Composable
private fun AiringStatusIcon(
    airingStatus: AiringStatus,
    itemColor: Color,
    backgroundColor: Color,
    modifier: Modifier = Modifier
) {
    val size = 32.dp

    when(airingStatus) {
        AiringStatus.AIRED -> {
            Icon(
                painter = painterResource(R.drawable.ic_clock_check),
                contentDescription = "Already Aired",
                tint = itemColor,
                modifier = modifier
                    .size(size)
                    .clip(CircleShape)
                    .background(backgroundColor)
                    .padding(all = 4.dp)
            )
        }
        AiringStatus.AIRING -> {
            PulseIndicator(
                backgroundColor = backgroundColor,
                itemColor = itemColor,
                modifier = Modifier.size(size)
            )
        }
        AiringStatus.NOT_YET_AIRED -> {
            Box(
                modifier = modifier
                    .size(size)
                    .background(backgroundColor, CircleShape)
                    .padding(all = 8.dp)
                    .background(itemColor, CircleShape)
            )
        }
    }
}