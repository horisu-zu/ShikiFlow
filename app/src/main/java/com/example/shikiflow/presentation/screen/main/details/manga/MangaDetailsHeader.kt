package com.example.shikiflow.presentation.screen.main.details.manga

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.outlined.Clear
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.graphql.MangaDetailsQuery
import com.example.graphql.type.MangaStatusEnum
import com.example.graphql.type.UserRateStatusEnum
import com.example.shikiflow.R
import com.example.shikiflow.domain.model.mapper.UserRateMapper
import com.example.shikiflow.domain.model.mapper.UserRateMapper.Companion.mapMangaKind
import com.example.shikiflow.domain.model.mapper.UserRateMapper.Companion.mapMangaStatus
import com.example.shikiflow.domain.model.tracks.MediaType
import com.example.shikiflow.domain.model.tracks.RateStatus
import com.example.shikiflow.presentation.common.CardItem
import com.example.shikiflow.presentation.common.image.BaseImage
import com.example.shikiflow.presentation.common.image.GradientImage
import com.example.shikiflow.presentation.common.image.ImageType
import com.example.shikiflow.presentation.screen.main.details.anime.ScoreItem
import com.example.shikiflow.presentation.screen.main.details.anime.ShortInfoItem
import com.example.shikiflow.utils.Converter.formatInstant
import com.example.shikiflow.utils.Converter.isManga
import com.example.shikiflow.utils.IconResource
import com.example.shikiflow.utils.Resource
import com.example.shikiflow.utils.StatusColor
import com.example.shikiflow.utils.toIcon
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn

@Composable
fun MangaDetailsHeader(
    mangaDetails: MangaDetailsQuery.Manga?,
    mangaDexResource: Resource<List<String>>,
    onStatusClick: () -> Unit,
    onMangaDexNavigateClick: (String) -> Unit,
    onMangaDexRefreshClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxWidth()) {
        GradientImage(
            model = mangaDetails?.poster?.posterShort?.originalUrl,
            modifier = Modifier.alpha(0.25f),
            gradientFraction = 0.8f
        )

        BaseImage(
            model = mangaDetails?.poster?.posterShort?.originalUrl,
            modifier = Modifier.align(Alignment.TopCenter),
            imageType = ImageType.Poster(
                defaultWidth = 240.dp
            )
        )

        Column(
            modifier = Modifier.align(Alignment.BottomCenter)
                .padding(horizontal = 12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            if (mangaDetails?.status != MangaStatusEnum.anons) {
                ScoreItem(score = mangaDetails?.score?.toFloat() ?: 0f)
            }

            Text(
                text = mangaDetails?.name ?: "",
                style = MaterialTheme.typography.headlineSmall
            )

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                ShortInfoItem(
                    infoType = stringResource(R.string.details_short_info_manga_type),
                    infoItem = "${mapMangaKind(mangaDetails?.kind)} ∙ ${mapMangaStatus(mangaDetails?.status)}"
                )
                if (mangaDetails?.status != MangaStatusEnum.ongoing && mangaDetails?.status != MangaStatusEnum.anons) {
                    mangaDetails?.releasedOn?.date?.let {
                        ShortInfoItem(
                            infoType = stringResource(R.string.details_short_info_manga_published),
                            infoItem = formatInstant(
                                LocalDate.parse(mangaDetails.releasedOn.date.toString())
                                    .atStartOfDayIn(TimeZone.currentSystemDefault()),
                                includeTime = false
                            )
                        )
                    }
                    mangaDetails?.volumes?.let { volumes ->
                        ShortInfoItem(
                            infoType = stringResource(R.string.details_short_info_manga_volumes),
                            infoItem = stringResource(R.string.volumes, volumes)
                        )
                    }
                    mangaDetails?.chapters?.let { chapters ->
                        ShortInfoItem(
                            infoType = stringResource(R.string.details_short_info_manga_chapters),
                            infoItem = stringResource(R.string.chapters, chapters)
                        )
                    }
                } else {
                    mangaDetails.airedOn?.date?.let {
                        ShortInfoItem(
                            infoType = stringResource(R.string.details_short_info_manga_started),
                            infoItem = formatInstant(
                                LocalDate.parse(it.toString())
                                    .atStartOfDayIn(TimeZone.currentSystemDefault()),
                                includeTime = false
                            )
                        )
                    }
                }
            }

            MangaUserRateItem(
                userRate = mangaDetails?.userRate?.status,
                allChapters = mangaDetails?.chapters ?: 0,
                readChapters = mangaDetails?.userRate?.chapters ?: 0,
                score = mangaDetails?.userRate?.score ?: 0,
                isManga = mangaDetails?.kind?.isManga() ?: false,
                mangaDexResource = mangaDexResource,
                onStatusClick = { onStatusClick() },
                onMangaDexNavigateClick = { onMangaDexNavigateClick(mangaDetails?.name ?: "") },
                onMangaDexRefreshClick = onMangaDexRefreshClick
            )

            LazyRow(
                modifier = Modifier.padding(top = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(mangaDetails?.genres ?: emptyList()) { genreItem ->
                    CardItem(genreItem.name)
                }
            }
        }
    }
}

@Composable
fun MangaUserRateItem(
    userRate: UserRateStatusEnum?,
    allChapters: Int,
    readChapters: Int,
    score: Int,
    isManga: Boolean,
    mangaDexResource: Resource<List<String>>,
    onStatusClick: () -> Unit,
    onMangaDexNavigateClick: () -> Unit,
    onMangaDexRefreshClick: () -> Unit,
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
                    color = StatusColor.getAnimeStatusColor(
                        userRate ?: UserRateStatusEnum.UNKNOWN__
                    ),
                    shape = RoundedCornerShape(12.dp)
                )
                .clickable { onStatusClick() }
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.Start),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val color = StatusColor.getAnimeStatusColor(userRate ?: UserRateStatusEnum.UNKNOWN__)
            val icon = RateStatus.fromStatus(userRate ?: UserRateStatusEnum.UNKNOWN__)?.icon
                ?: IconResource.Vector(Icons.Outlined.Clear)

            icon.toIcon(
                modifier = Modifier.size(24.dp),
                tint = color
            )
            Text(
                text = UserRateMapper.mapStatusToString(
                    status = userRate ?: UserRateStatusEnum.UNKNOWN__,
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
                    .clickable {
                        if (mangaDexResource is Resource.Success) onMangaDexNavigateClick()
                        else if (mangaDexResource is Resource.Error) onMangaDexRefreshClick()
                    }
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
                        Icon(
                            painter = painterResource(id = R.drawable.ic_mangadex_v2),
                            contentDescription = null,
                            modifier = Modifier.size(24.dp)
                        )
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