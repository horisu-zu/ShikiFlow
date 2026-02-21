package com.example.shikiflow.presentation.screen.main.details.manga

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.shikiflow.domain.model.track.UserRateStatus
import com.example.shikiflow.R
import com.example.shikiflow.domain.model.mapper.UserRateMapper
import com.example.shikiflow.domain.model.media_details.MediaDetails
import com.example.shikiflow.domain.model.media_details.MediaStatus
import com.example.shikiflow.domain.model.tracks.MediaType
import com.example.shikiflow.domain.model.tracks.UserRateIconProvider.icon
import com.example.shikiflow.presentation.common.CardItem
import com.example.shikiflow.presentation.common.image.BaseImage
import com.example.shikiflow.presentation.common.image.GradientImage
import com.example.shikiflow.presentation.common.image.ImageType
import com.example.shikiflow.presentation.screen.main.details.anime.ScoreItem
import com.example.shikiflow.presentation.screen.main.details.anime.ShortInfoItem
import com.example.shikiflow.utils.Converter.formatInstant
import com.example.shikiflow.utils.Converter.isManga
import com.example.shikiflow.utils.Resource
import com.example.shikiflow.utils.StatusColor
import com.example.shikiflow.utils.ignoreHorizontalParentPadding
import com.example.shikiflow.utils.toIcon

@Composable
fun MangaDetailsHeader(
    mangaDetails: MediaDetails,
    mangaDexResource: Resource<List<String>>,
    horizontalPadding: Dp,
    onStatusClick: () -> Unit,
    onMangaDexIconClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val orientation = LocalConfiguration.current.orientation

    Box(modifier = modifier.fillMaxWidth()) {
        GradientImage(
            model = mangaDetails.coverImageUrl,
            gradientFraction = 0.8f,
            imageType = ImageType.Poster(
                defaultClip = RoundedCornerShape(0.dp),
                defaultAspectRatio = if(orientation == Configuration.ORIENTATION_PORTRAIT)
                    2f / 2.85f else 1.5f
            ),
            modifier = Modifier.ignoreHorizontalParentPadding(horizontalPadding)
        )

        Column(
            modifier = Modifier.align(Alignment.BottomCenter).statusBarsPadding(),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            BaseImage(
                model = mangaDetails.coverImageUrl,
                imageType = ImageType.Poster(
                    defaultWidth = 216.dp
                ),
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

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
                        append(stringResource(mangaDetails.format.displayValue))
                        append(" ∙ ")
                        append(stringResource(id = mangaDetails.status.displayValue))
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
                mangaDexResource = mangaDexResource,
                onStatusClick = { onStatusClick() },
                onMangaDexIconClick = onMangaDexIconClick,
            )

            LazyRow(
                modifier = Modifier.padding(top = 4.dp)
                    .ignoreHorizontalParentPadding(horizontalPadding)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = horizontalPadding),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(mangaDetails.genres) { genreItem ->
                    CardItem(genreItem)
                }
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
    mangaDexResource: Resource<List<String>>,
    onStatusClick: () -> Unit,
    onMangaDexIconClick: () -> Unit,
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
                .background(MaterialTheme.colorScheme.background)
                .border(
                    width = 1.dp,
                    color = StatusColor.getStatusColor(
                        status = userRateStatus ?: UserRateStatus.UNKNOWN
                    ),
                    shape = RoundedCornerShape(12.dp)
                )
                .clickable { onStatusClick() }
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.Start),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val color = StatusColor.getStatusColor(userRateStatus ?: UserRateStatus.UNKNOWN)

            userRateStatus.icon(MediaType.MANGA).toIcon(
                modifier = Modifier.size(24.dp),
                tint = color
            )
            Text(
                text = UserRateMapper.mapUserRateStatusToString(
                    status = userRateStatus ?: UserRateStatus.UNKNOWN,
                    allEpisodes = allChapters,
                    watchedEpisodes = readChapters,
                    score = score,
                    mediaType = MediaType.MANGA
                ),
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = color,
                    fontWeight = FontWeight.SemiBold
                )
            )
        }
        if(isManga) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .clickable(
                        enabled = mangaDexResource !is Resource.Loading,
                        onClick = { onMangaDexIconClick() }
                    )
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .padding(12.dp),
                contentAlignment = Alignment.Center
            ) {
                when(mangaDexResource) {
                    is Resource.Loading -> {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            strokeWidth = 3.dp
                        )
                    }
                    is Resource.Success -> {
                        if(mangaDexResource.data.isNullOrEmpty()) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_circle),
                                contentDescription = "Empty Response",
                                modifier = Modifier.size(24.dp)
                            )
                        } else {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_mangadex_v2),
                                contentDescription = "Manga Read Navigate",
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                    is Resource.Error -> {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = null,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        }
    }
}