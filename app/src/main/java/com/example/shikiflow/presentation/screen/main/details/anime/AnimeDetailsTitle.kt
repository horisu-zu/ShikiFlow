package com.example.shikiflow.presentation.screen.main.details.anime

import android.content.res.Configuration
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.shikiflow.domain.model.track.UserRateStatus
import com.example.shikiflow.R
import com.example.shikiflow.domain.model.auth.AuthType
import com.example.shikiflow.domain.model.media_details.MediaDetails
import com.example.shikiflow.domain.model.media_details.MediaStatus
import com.example.shikiflow.domain.model.media_details.MediaTitle.Companion.preferred
import com.example.shikiflow.domain.model.media_details.PreferredTitleType
import com.example.shikiflow.domain.model.track.media.MediaUserTrack
import com.example.shikiflow.domain.model.tracks.MediaType
import com.example.shikiflow.presentation.common.SnapFlingLazyRow
import com.example.shikiflow.presentation.common.mappers.UserRateIconProvider.icon
import com.example.shikiflow.presentation.common.StarScore
import com.example.shikiflow.presentation.common.image.BaseImage
import com.example.shikiflow.presentation.common.image.ImageType
import com.example.shikiflow.presentation.common.mappers.AgeRatingMapper.displayValue
import com.example.shikiflow.presentation.common.mappers.MediaFormatMapper.displayValue
import com.example.shikiflow.presentation.common.mappers.MediaStatusMapper.displayValue
import com.example.shikiflow.presentation.common.mappers.UserRateStatusMapper
import com.example.shikiflow.presentation.common.foregroundGradient
import com.example.shikiflow.presentation.common.ignoreHorizontalParentPadding
import com.example.shikiflow.presentation.common.mappers.DateMapper.isAiring
import com.example.shikiflow.presentation.common.mappers.DateMapper.toCountdownString
import com.example.shikiflow.presentation.common.mappers.DateMapper.untilNextEpisode
import com.example.shikiflow.utils.DateUtils.calculateDuration
import com.example.shikiflow.utils.toIcon
import kotlinx.coroutines.delay
import kotlin.time.Clock
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds
import kotlin.time.Instant

@Composable
fun AnimeDetailsTitle(
    animeDetails: MediaDetails,
    userRate: MediaUserTrack?,
    authType: AuthType,
    titleType: PreferredTitleType,
    horizontalPadding: Dp,
    onStatusClick: () -> Unit,
    onPlayClick: (String, Int, Int) -> Unit,
    onToggleFavorite: () -> Unit,
    modifier: Modifier = Modifier
) {
    val orientation = LocalConfiguration.current.orientation
    val isLandscape = orientation == Configuration.ORIENTATION_LANDSCAPE

    Box(modifier = modifier.fillMaxWidth()) {
        BaseImage(
            model = animeDetails.coverImageUrl,
            imageType = ImageType.Poster(
                width = Int.MAX_VALUE.dp,
                clip = RoundedCornerShape(0.dp),
                aspectRatio = if(!isLandscape) 2f / 2.85f
                    else 2.25f
            ),
            modifier = Modifier
                .ignoreHorizontalParentPadding(horizontalPadding)
                .foregroundGradient(
                    gradientColors = listOf(
                        Color.Transparent,
                        MaterialTheme.colorScheme.background
                    ),
                    gradientFraction = 0.9f
                )
        )

        Row(
            modifier = Modifier.align(Alignment.BottomCenter),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.Bottom
        ) {
            if(isLandscape) {
                BaseImage(
                    model = animeDetails.coverImageUrl,
                    imageType = ImageType.Poster(
                        width = 120.dp
                    )
                )
            }
            Column {
                if (animeDetails.status != MediaStatus.ANNOUNCED && animeDetails.score != null
                    && animeDetails.score != 0.0f
                ) {
                    ScoreItem(
                        score = animeDetails.score,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }

                Text(
                    text = animeDetails.title.preferred(titleType),
                    style = MaterialTheme.typography.headlineSmall
                )

                SnapFlingLazyRow(
                    modifier = Modifier
                        .ignoreHorizontalParentPadding(horizontalPadding)
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    contentPadding = PaddingValues(horizontal = horizontalPadding),
                    horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.Start)
                ) {
                    item {
                        ShortInfoItem(
                            infoType = stringResource(id = R.string.details_short_info_type),
                            infoItem = buildString {
                                append(stringResource(id = animeDetails.format.displayValue()))
                                append(" ∙ ")
                                append(stringResource(id = animeDetails.status.displayValue()))
                            }
                        )
                    }
                    item {
                        if(animeDetails.totalCount == 1 && animeDetails.durationMins != null) {
                            val (hours, minutes) = animeDetails.durationMins.calculateDuration()

                            ShortInfoItem(
                                infoType = stringResource(id = R.string.details_info_duration),
                                infoItem = when(hours) {
                                    0 -> stringResource(
                                        id = R.string.details_info_duration_min,
                                        minutes
                                    )
                                    else -> stringResource(
                                        id = R.string.details_info_duration_hours,
                                        hours, minutes
                                    )
                                }
                            )
                        } else {
                            ShortInfoItem(
                                infoType = stringResource(id = R.string.details_short_info_episodes),
                                infoItem = if (animeDetails.status != MediaStatus.ONGOING) {
                                    stringResource(
                                        id = R.string.episodes,
                                        animeDetails.totalCount.takeIf { it != 0 } ?: "?"
                                    )
                                } else {
                                    stringResource(
                                        id = R.string.ongoing_episodes,
                                        animeDetails.currentProgress ?: 0,
                                        animeDetails.totalCount.takeIf { it != 0 } ?: "?"
                                    )
                                }
                            )
                        }
                    }
                    animeDetails.ageRating?.let { ageRating ->
                        item {
                            ShortInfoItem(
                                infoType = stringResource(id = R.string.details_short_info_age_rating),
                                infoItem = stringResource(ageRating.displayValue())
                            )
                        }
                    }
                    if(animeDetails.status == MediaStatus.ONGOING) {
                        item {
                            val isAiring = animeDetails.nextEpisodeAt?.isAiring(
                                startDate = animeDetails.airedOn?.date ?: Clock.System.now(),
                                episodeDuration = animeDetails.durationMins?.minutes ?: 0.minutes
                            ) == true

                            if (isAiring) {
                                animeDetails.durationMins?.let { durationMins ->
                                    AiringCountdown(durationMins, animeDetails.nextEpisodeAt)
                                }
                            } else {
                                animeDetails.nextEpisodeAt?.let { nextEpisodeAt ->
                                    ShortInfoItem(
                                        infoType = stringResource(R.string.details_short_info_airing_in),
                                        infoItem = nextEpisodeAt.untilNextEpisode()
                                    )
                                }
                            }
                        }
                    }
                }

                UserStatusItem(
                    authType = authType,
                    isFavorite = animeDetails.isFavorite,
                    onStatusClick = onStatusClick,
                    onPlayClick = { onPlayClick(
                        animeDetails.title.preferred(titleType),
                        animeDetails.id,
                        userRate?.progress ?: 0
                    ) },
                    onToggleFavorite = onToggleFavorite,
                    status = userRate?.status,
                    allEpisodes = (
                        if(animeDetails.status == MediaStatus.RELEASED) animeDetails.totalCount
                            else animeDetails.currentProgress
                    ) ?: 0,
                    watchedEpisodes = userRate?.progress,
                    score = userRate?.score
                )
            }
        }
    }
}

@Composable
fun ScoreItem(
    score: Float,
    modifier: Modifier = Modifier
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
    authType: AuthType,
    status: UserRateStatus?,
    allEpisodes: Int,
    watchedEpisodes: Int?,
    score: Int?,
    isFavorite: Boolean?,
    onStatusClick: () -> Unit,
    onPlayClick: () -> Unit,
    onToggleFavorite: () -> Unit,
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
            status.icon(MediaType.ANIME).toIcon(
                modifier = Modifier.size(24.dp),
                tint = MaterialTheme.colorScheme.surface
            )
            Text(
                text = UserRateStatusMapper.mapUserRateStatusToString(
                    status = status ?: UserRateStatus.UNKNOWN,
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
        if(authType == AuthType.SHIKIMORI) {
            HeaderButton(
                onClick = onPlayClick
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.size(24.dp)
                )
            }
        } else if(isFavorite != null) {
            val iconTint by animateColorAsState(
                targetValue = when(isFavorite) {
                    true -> MaterialTheme.colorScheme.error
                    false -> MaterialTheme.colorScheme.onPrimaryContainer
                },
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioNoBouncy,
                    stiffness = Spring.StiffnessMediumLow
                )
            )

            HeaderButton(
                onClick = onToggleFavorite
            ) {
                Icon(
                    imageVector = Icons.Default.Favorite,
                    tint = iconTint,
                    contentDescription = "Favorite Icon"
                )
            }
        }
    }
}

@Composable
fun HeaderButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable(
                enabled = enabled,
                onClick = onClick
            )
            .background(MaterialTheme.colorScheme.primaryContainer)
            .padding(12.dp),
        contentAlignment = Alignment.Center
    ) {
        content()
    }
}

@Composable
fun AiringCountdown(
    durationMins: Int,
    airingAt: Instant
) {
    var countdownString by remember {
        mutableStateOf(durationMins.minutes.toCountdownString(airingAt))
    }

    LaunchedEffect(Unit) {
        while (true) {
            delay(1.seconds)
            countdownString = durationMins.minutes.toCountdownString(airingAt)
        }
    }

    ShortInfoItem(
        infoType = stringResource(R.string.details_short_info_airing_now),
        infoItem = countdownString
    )
}