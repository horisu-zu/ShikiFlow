package com.example.shikiflow.presentation.screen.main.details.anime

import android.content.res.Configuration
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
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.shikiflow.domain.model.track.UserRateStatus
import com.example.shikiflow.R
import com.example.shikiflow.domain.model.auth.AuthType
import com.example.shikiflow.domain.model.mapper.UserRateMapper
import com.example.shikiflow.domain.model.media_details.MediaDetails
import com.example.shikiflow.domain.model.media_details.MediaStatus
import com.example.shikiflow.domain.model.tracks.MediaType
import com.example.shikiflow.domain.model.tracks.UserRateIconProvider.icon
import com.example.shikiflow.presentation.common.StarScore
import com.example.shikiflow.presentation.common.image.GradientImage
import com.example.shikiflow.presentation.common.image.ImageType
import com.example.shikiflow.utils.ignoreHorizontalParentPadding
import com.example.shikiflow.utils.toIcon

@Composable
fun AnimeDetailsTitle(
    animeDetails: MediaDetails,
    authType: AuthType,
    horizontalPadding: Dp,
    onStatusClick: () -> Unit,
    onPlayClick: (String, Int, Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val orientation = LocalConfiguration.current.orientation

    Box(modifier = modifier.fillMaxWidth()) {
        GradientImage(
            model = animeDetails.coverImageUrl,
            gradientFraction = 0.9f,
            imageType = ImageType.Poster(
                defaultClip = RoundedCornerShape(0.dp),
                defaultAspectRatio = if(orientation == Configuration.ORIENTATION_PORTRAIT)
                    2f / 2.85f else 2.25f
            ),
            modifier = Modifier.ignoreHorizontalParentPadding(horizontalPadding)
        )

        Column(
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            if (animeDetails.status != MediaStatus.ANNOUNCED && animeDetails.score != null
                && animeDetails.score != 0.0f
            ) {
                ScoreItem(
                    score = animeDetails.score,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }

            Text(
                text = animeDetails.title,
                style = MaterialTheme.typography.headlineSmall
            )

            Row(
                modifier = Modifier.padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ShortInfoItem(
                    infoType = stringResource(id = R.string.details_short_info_type),
                    infoItem = buildString {
                        append(stringResource(id = animeDetails.format.displayValue))
                        append(" ∙ ")
                        append(stringResource(id = animeDetails.status.displayValue))
                    }
                )
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
                animeDetails.mediaAgeRating?.let { ratingEnum ->
                    ShortInfoItem(
                        infoType = stringResource(id = R.string.details_short_info_age_rating),
                        infoItem = stringResource(ratingEnum.displayValue)
                    )
                }
            }

            UserStatusItem(
                authType = authType,
                onStatusClick = onStatusClick,
                onPlayClick = { onPlayClick(
                    animeDetails.title,
                    animeDetails.id,
                    animeDetails.userRate?.progress ?: 0
                ) },
                status = animeDetails.userRate?.rateStatus,
                allEpisodes = (
                    if(animeDetails.status == MediaStatus.RELEASED) animeDetails.totalCount
                        else animeDetails.currentProgress
                ) ?: 0,
                watchedEpisodes = animeDetails.userRate?.progress,
                score = animeDetails.userRate?.score
            )
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
            status.icon(MediaType.ANIME).toIcon(
                modifier = Modifier.size(24.dp),
                tint = MaterialTheme.colorScheme.surface
            )
            Text(
                text = UserRateMapper.mapUserRateStatusToString(
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
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { onPlayClick() }
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .padding(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}