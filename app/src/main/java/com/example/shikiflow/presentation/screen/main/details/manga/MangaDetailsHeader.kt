package com.example.shikiflow.presentation.screen.main.details.manga

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Clear
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.example.graphql.MangaDetailsQuery
import com.example.graphql.type.MangaStatusEnum
import com.example.graphql.type.UserRateStatusEnum
import com.example.shikiflow.data.mapper.UserRateMapper
import com.example.shikiflow.data.mapper.UserRateMapper.Companion.mapMangaKind
import com.example.shikiflow.data.mapper.UserRateMapper.Companion.mapMangaStatus
import com.example.shikiflow.data.tracks.MediaType
import com.example.shikiflow.data.tracks.RateStatus
import com.example.shikiflow.presentation.common.CardItem
import com.example.shikiflow.presentation.common.image.BaseImage
import com.example.shikiflow.presentation.common.image.GradientImage
import com.example.shikiflow.presentation.common.image.ImageType
import com.example.shikiflow.presentation.screen.main.details.anime.ScoreItem
import com.example.shikiflow.presentation.screen.main.details.anime.ShortInfoItem
import com.example.shikiflow.utils.Converter.formatInstant
import com.example.shikiflow.utils.IconResource
import com.example.shikiflow.utils.StatusColor
import com.example.shikiflow.utils.toIcon
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn

@Composable
fun MangaDetailsHeader(
    mangaDetails: MangaDetailsQuery.Manga?,
    onStatusClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    ConstraintLayout(
        modifier = modifier.fillMaxWidth()
    ) {
        val (backgroundRef, posterRef, scoreRef, titleRef, infoRow, statusItem, genresRow) = createRefs()

        GradientImage(
            model = mangaDetails?.poster?.posterShort?.originalUrl,
            modifier = Modifier
                .constrainAs(backgroundRef) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    bottom.linkTo(parent.bottom)
                }.alpha(0.25f),
            gradientFraction = 0.8f
        )

        BaseImage(
            model = mangaDetails?.poster?.posterShort?.originalUrl,
            modifier = Modifier.constrainAs(posterRef) {
                bottom.linkTo(scoreRef.top, margin = 12.dp)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            },
            contentScale = ContentScale.Fit,
            imageType = ImageType.Poster(
                defaultWidth = 240.dp,
                defaultClip = RoundedCornerShape(16.dp)
            ),
        )

        if (mangaDetails?.status != MangaStatusEnum.anons) {
            ScoreItem(
                score = mangaDetails?.score?.toFloat() ?: 0f,
                modifier = Modifier
                    .constrainAs(scoreRef) {
                        bottom.linkTo(titleRef.top)
                        start.linkTo(parent.start)
                    }.padding(horizontal = 12.dp)
            )
        }

        Text(
            text = mangaDetails?.name ?: "",
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
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ShortInfoItem(
                infoType = "Type",
                infoItem = "${mapMangaKind(mangaDetails?.kind)} ∙ ${mapMangaStatus(mangaDetails?.status)}"
            )
            if(mangaDetails?.status != MangaStatusEnum.ongoing && mangaDetails?.status != MangaStatusEnum.anons) {
                mangaDetails?.releasedOn?.date?.let {
                    ShortInfoItem(
                        infoType = "Published",
                        infoItem = formatInstant(
                            LocalDate.parse(mangaDetails.releasedOn.date.toString())
                                .atStartOfDayIn(TimeZone.currentSystemDefault()),
                            includeTime = false
                        )
                    )
                }
                ShortInfoItem(
                    infoType = "Volumes",
                    infoItem = "${mangaDetails?.volumes} vol."
                )
                ShortInfoItem(
                    infoType = "Chapters",
                    infoItem = "${mangaDetails?.chapters} ch."
                )
            } else {
                mangaDetails.airedOn?.date?.let {
                    ShortInfoItem(
                        infoType = "Started",
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
            onStatusClick = { onStatusClick() },
            modifier = Modifier.constrainAs(statusItem) {
                bottom.linkTo(genresRow.top, margin = 4.dp)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                width = Dimension.fillToConstraints
            }.padding(horizontal = 12.dp)
        )

        LazyRow(
            modifier = Modifier.constrainAs(genresRow) {
                bottom.linkTo(parent.bottom)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                width = Dimension.fillToConstraints
            }.padding(horizontal = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(mangaDetails?.genres ?: emptyList()) { genreItem ->
                CardItem(genreItem.name)
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
    onStatusClick: () -> Unit,
    modifier: Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.background)
            .border(
                width = 1.dp,
                color = StatusColor.getAnimeStatusColor(userRate ?: UserRateStatusEnum.UNKNOWN__),
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
}