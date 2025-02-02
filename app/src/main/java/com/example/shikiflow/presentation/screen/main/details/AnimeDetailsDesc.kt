package com.example.shikiflow.presentation.screen.main.details

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.example.graphql.AnimeDetailsQuery
import com.example.shikiflow.data.mapper.UserRateMapper.Companion.mapAnimeKind
import com.example.shikiflow.data.mapper.UserRateMapper.Companion.mapMangaKind
import com.example.shikiflow.data.mapper.UserRateMapper.Companion.mapRelationKind
import com.example.shikiflow.presentation.common.CardItem
import com.example.shikiflow.presentation.common.FormattedText
import com.example.shikiflow.presentation.common.image.BaseImage
import com.example.shikiflow.presentation.common.image.RoundedImage
import com.example.shikiflow.presentation.common.image.ImageType

@Composable
fun AnimeDetailsDesc(
    animeDetails: AnimeDetailsQuery.Anime?,
    modifier: Modifier = Modifier
) {
    var showRelatedBottomSheet by remember { mutableStateOf(false) }

    ConstraintLayout(
        modifier = modifier.fillMaxWidth()
    ) {
        val (descRef, genresRef, charactersRef, relatedRef, screenshotsRef, additionalRef) = createRefs()

        FormattedText(
            text = animeDetails?.description ?: "No Description",
            modifier = Modifier.constrainAs(descRef) {
                top.linkTo(parent.top)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                width = Dimension.fillToConstraints
            },
            style = MaterialTheme.typography.bodySmall,
            linkColor = MaterialTheme.colorScheme.primary,
            brushColor = MaterialTheme.colorScheme.background.copy(0.8f),
            onClick = { id ->
                Log.d("Details Screen", "Clicked id: $id")
            }
        )

        LazyRow(
            modifier = Modifier.constrainAs(genresRef) {
                top.linkTo(descRef.bottom, margin = 2.dp)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                width = Dimension.fillToConstraints
            },
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(animeDetails?.genres ?: emptyList()) { genreItem ->
                CardItem(genreItem.name)
            }
        }

        Column(
            modifier = Modifier.constrainAs(charactersRef) {
                top.linkTo(genresRef.bottom, margin = 8.dp)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                width = Dimension.fillToConstraints
            },
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Characters",
                style = MaterialTheme.typography.titleMedium
            )
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(animeDetails?.characterRoles ?: emptyList()) { characterItem ->
                    CharacterCard(
                        character = characterItem.character,
                        onClick = { /*TODO*/ }
                    )
                }
            }
        }

        Column(
            modifier = Modifier.constrainAs(relatedRef) {
                top.linkTo(charactersRef.bottom, margin = 12.dp)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                width = Dimension.fillToConstraints
            },
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Related",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.clickable { showRelatedBottomSheet = true }
            )
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                animeDetails?.related?.take(3)?.forEach { relatedItem ->
                    RelatedItem(
                        relatedInfo = relatedItem
                    )
                }
            }
        }

        ScreenshotSection(
            screenshots = animeDetails?.screenshots ?: emptyList(),
            modifier = Modifier.constrainAs(screenshotsRef) {
                top.linkTo(relatedRef.bottom, margin = 12.dp)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                width = Dimension.fillToConstraints
            }
        )

        animeDetails?.let {
            AnimeDetailsInfo(
                animeDetails = it,
                modifier = Modifier.constrainAs(additionalRef) {
                    top.linkTo(screenshotsRef.bottom, margin = 12.dp)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    width = Dimension.fillToConstraints
                }
            )
        }
    }

    RelatedBottomSheet(
        relatedItems = animeDetails?.related,
        showBottomSheet = showRelatedBottomSheet,
        onDismiss = { showRelatedBottomSheet = false }
    )
}

@Composable
fun CharacterCard(
    character: AnimeDetailsQuery.Character,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .width(96.dp)
            .clip(RoundedCornerShape(8.dp))
            .clickable { onClick() },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        RoundedImage(
            model = character.characterShort.poster?.posterShort?.previewUrl,
            size = 96.dp,
            clip = CircleShape,
            contentScale = ContentScale.Crop
        )
        Text(
            text = character.characterShort.name,
            overflow = TextOverflow.Ellipsis,
            maxLines = 1,
            style = MaterialTheme.typography.labelMedium.copy(fontSize = 11.sp)
        )
    }
}

@Composable
fun RelatedItem(
    relatedInfo: AnimeDetailsQuery.Related,
    modifier: Modifier = Modifier
) {
    ConstraintLayout(
        modifier = modifier.fillMaxWidth()
    ) {
        val (posterRef, titleRef, infoRef) = createRefs()

        RoundedImage(
            model = if (relatedInfo.anime != null) relatedInfo.anime.poster?.mainUrl
            else relatedInfo.manga?.poster?.mainUrl ?: "Manga Poster",
            clip = RoundedCornerShape(8.dp),
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(48.dp)
                .constrainAs(posterRef) {
                    start.linkTo(parent.start)
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                }
        )

        Text(
            text = if (relatedInfo.anime != null) relatedInfo.anime.name
            else relatedInfo.manga?.name ?: "Manga Title",
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.constrainAs(titleRef) {
                top.linkTo(parent.top)
                bottom.linkTo(infoRef.top)
                start.linkTo(posterRef.end, margin = 12.dp)
                end.linkTo(parent.end)
                width = Dimension.fillToConstraints
            }
        )

        Text(
            text = if (relatedInfo.anime != null) {
                mapAnimeKind(relatedInfo.anime.kind)
            } else {
                mapMangaKind(relatedInfo.manga?.kind)
            } + " ∙ ${mapRelationKind(relatedInfo.relationKind)}",
            style = MaterialTheme.typography.labelSmall.copy(
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
            ),
            modifier = Modifier.constrainAs(infoRef) {
                top.linkTo(titleRef.bottom)
                bottom.linkTo(parent.bottom)
                start.linkTo(titleRef.start)
                end.linkTo(parent.end)
                width = Dimension.fillToConstraints
            }
        )
    }
}

@Composable
fun ScreenshotSection(
    screenshots: List<AnimeDetailsQuery.Screenshot>,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "Screenshots",
            style = MaterialTheme.typography.titleMedium
        )
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(screenshots) { screenshot ->
                BaseImage(
                    model = screenshot.originalUrl,
                    modifier = Modifier.width(280.dp),
                    imageType = ImageType.Screenshot()
                )
            }
        }
    }
}