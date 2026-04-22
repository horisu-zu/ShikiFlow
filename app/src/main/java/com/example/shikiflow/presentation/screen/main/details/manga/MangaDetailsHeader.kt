package com.example.shikiflow.presentation.screen.main.details.manga

import android.content.res.Configuration
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.shikiflow.domain.model.track.UserRateStatus
import com.example.shikiflow.R
import com.example.shikiflow.domain.model.media_details.MediaDetails
import com.example.shikiflow.domain.model.media_details.MediaStatus
import com.example.shikiflow.domain.model.tracks.MediaType
import com.example.shikiflow.presentation.common.mappers.UserRateIconProvider.icon
import com.example.shikiflow.presentation.common.image.BaseImage
import com.example.shikiflow.presentation.common.image.ImageType
import com.example.shikiflow.presentation.common.mappers.ColorMapper.color
import com.example.shikiflow.presentation.common.mappers.MediaFormatMapper.displayValue
import com.example.shikiflow.presentation.common.mappers.MediaStatusMapper.displayValue
import com.example.shikiflow.presentation.common.mappers.UserRateStatusMapper
import com.example.shikiflow.presentation.screen.main.details.anime.HeaderButton
import com.example.shikiflow.presentation.screen.main.details.anime.ScoreItem
import com.example.shikiflow.presentation.screen.main.details.anime.ShortInfoItem
import com.example.shikiflow.presentation.viewmodel.manga.details.MangaDexUiState
import com.example.shikiflow.utils.Converter.formatInstant
import com.example.shikiflow.utils.Converter.isManga
import com.example.shikiflow.utils.foregroundGradient
import com.example.shikiflow.utils.ignoreHorizontalParentPadding
import com.example.shikiflow.utils.toIcon
import com.materialkolor.ktx.harmonize

@Composable
fun MangaDetailsHeader(
    mangaDetails: MediaDetails,
    mangaDexUiState: MangaDexUiState,
    horizontalPadding: Dp,
    onStatusClick: () -> Unit,
    onMangaDexIconClick: () -> Unit,
    onToggleFavorite: () -> Unit,
    modifier: Modifier = Modifier
) {
    val orientation = LocalConfiguration.current.orientation
    val isLandscape = orientation == Configuration.ORIENTATION_LANDSCAPE

    Box(modifier = modifier.fillMaxWidth()) {
        BaseImage(
            model = mangaDetails.coverImageUrl,
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
                    model = mangaDetails.coverImageUrl,
                    imageType = ImageType.Poster(
                        width = 120.dp
                    )
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                if (mangaDetails.status != MediaStatus.ANNOUNCED && mangaDetails.score != null
                    && mangaDetails.score != 0.0f
                ) {
                    ScoreItem(score = mangaDetails.score)
                }

                Text(
                    text = mangaDetails.title,
                    style = MaterialTheme.typography.headlineSmall
                )

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    ShortInfoItem(
                        infoType = stringResource(R.string.details_short_info_manga_type),
                        infoItem = buildString {
                            append(stringResource(mangaDetails.format.displayValue()))
                            append(" ∙ ")
                            append(stringResource(id = mangaDetails.status.displayValue()))
                        }
                    )
                    if (mangaDetails.status != MediaStatus.ONGOING && mangaDetails.status != MediaStatus.ANNOUNCED) {
                        mangaDetails.releasedOn?.date?.let { releaseDate ->
                            ShortInfoItem(
                                infoType = stringResource(R.string.details_short_info_manga_published),
                                infoItem = formatInstant(
                                    instant = releaseDate,
                                    includeTime = false
                                )
                            )
                        }
                        if(mangaDetails.volumes != null) {
                            ShortInfoItem(
                                infoType = stringResource(R.string.details_short_info_manga_volumes),
                                infoItem = stringResource(R.string.volumes, mangaDetails.volumes)
                            )
                        }
                        if(mangaDetails.totalCount != null) {
                            ShortInfoItem(
                                infoType = stringResource(R.string.details_short_info_manga_chapters),
                                infoItem = stringResource(R.string.chapters, mangaDetails.totalCount)
                            )
                        }
                    } else {
                        mangaDetails.airedOn?.date?.let { startingDate ->
                            ShortInfoItem(
                                infoType = stringResource(R.string.details_short_info_manga_started),
                                infoItem = formatInstant(
                                    instant = startingDate,
                                    includeTime = false
                                )
                            )
                        }
                    }
                }

                MangaUserRateItem(
                    userRateStatus = mangaDetails.userRate?.rateStatus,
                    allChapters = mangaDetails.totalCount ?: 0,
                    readChapters = mangaDetails.userRate?.progress ?: 0,
                    score = mangaDetails.userRate?.score ?: 0,
                    isManga = mangaDetails.format.isManga(),
                    isFavorite = mangaDetails.isFavorite,
                    mangaDexUiState = mangaDexUiState,
                    onStatusClick = { onStatusClick() },
                    onMangaDexIconClick = onMangaDexIconClick,
                    onToggleFavorite = onToggleFavorite
                )
            }
        }
    }
}

@Composable
fun MangaUserRateItem(
    userRateStatus: UserRateStatus?,
    allChapters: Int,
    readChapters: Int,
    score: Int,
    isManga: Boolean,
    isFavorite: Boolean?,
    mangaDexUiState: MangaDexUiState,
    onStatusClick: () -> Unit,
    onMangaDexIconClick: () -> Unit,
    onToggleFavorite: () -> Unit,
    modifier: Modifier = Modifier
) {
    val rateColor = (userRateStatus ?: UserRateStatus.UNKNOWN).color()
        .harmonize(MaterialTheme.colorScheme.background)

    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.background)
                .border(
                    width = 1.dp,
                    color = rateColor,
                    shape = RoundedCornerShape(12.dp)
                )
                .clickable { onStatusClick() }
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.Start),
            verticalAlignment = Alignment.CenterVertically
        ) {
            userRateStatus.icon(MediaType.MANGA).toIcon(
                modifier = Modifier.size(24.dp),
                tint = rateColor
            )
            Text(
                text = UserRateStatusMapper.mapUserRateStatusToString(
                    status = userRateStatus ?: UserRateStatus.UNKNOWN,
                    allEpisodes = allChapters,
                    watchedEpisodes = readChapters,
                    score = score,
                    mediaType = MediaType.MANGA
                ),
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = rateColor,
                    fontWeight = FontWeight.SemiBold
                )
            )
        }
        if(isFavorite != null) {
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
        if(isManga) {
            HeaderButton(
                enabled = !mangaDexUiState.isLoading,
                onClick = onMangaDexIconClick
            ) {
                if(mangaDexUiState.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 3.dp
                    )
                } else if(mangaDexUiState.errorMessage != null) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.size(24.dp)
                    )
                } else {
                    if(mangaDexUiState.mangaDexIds.isNotEmpty()) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_mangadex_v2),
                            contentDescription = "Manga Read Navigate",
                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.size(24.dp)
                        )
                    } else {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_circle),
                            contentDescription = "Empty Response",
                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        }
    }
}