package com.example.shikiflow.presentation.screen.main.details.manga

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.example.graphql.MangaDetailsQuery
import com.example.graphql.type.MangaStatusEnum
import com.example.shikiflow.data.mapper.UserRateMapper.Companion.mapMangaKind
import com.example.shikiflow.data.mapper.UserRateMapper.Companion.mapMangaStatus
import com.example.shikiflow.presentation.common.CardItem
import com.example.shikiflow.presentation.common.image.BaseImage
import com.example.shikiflow.presentation.common.image.GradientImage
import com.example.shikiflow.presentation.common.image.ImageType
import com.example.shikiflow.presentation.screen.main.details.anime.ScoreItem
import com.example.shikiflow.presentation.screen.main.details.anime.ShortInfoItem
import com.example.shikiflow.utils.Converter.formatInstant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn

@Composable
fun MangaDetailsHeader(
    mangaDetails: MangaDetailsQuery.Manga?,
    //onStatusClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    ConstraintLayout(
        modifier = modifier.fillMaxWidth()
    ) {
        val (backgroundRef, posterRef, scoreRef, titleRef, infoRow, genresRow, statusItem) = createRefs()

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
                    bottom.linkTo(genresRow.top, margin = 4.dp)
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
                CardItem(
                    genreItem.name,
                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 8.dp)
                )
            }
        }
    }
}