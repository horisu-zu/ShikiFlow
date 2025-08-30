package com.example.shikiflow.presentation.screen.main.details.anime

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import com.example.shikiflow.domain.model.common.RelatedInfo
import com.example.shikiflow.domain.model.mapper.RelatedMapper
import com.example.shikiflow.domain.model.tracks.MediaType
import com.example.shikiflow.presentation.common.CardItem
import com.example.shikiflow.presentation.common.ExpandableText
import com.example.shikiflow.presentation.common.image.BaseImage
import com.example.shikiflow.presentation.common.image.RoundedImage
import com.example.shikiflow.presentation.common.image.ImageType
import com.example.shikiflow.presentation.screen.main.details.RelatedBottomSheet
import com.example.shikiflow.utils.Converter.EntityType

@Composable
fun AnimeDetailsDesc(
    animeDetails: AnimeDetailsQuery.Anime?,
    onSimilarClick: (String, String) -> Unit,
    onLinkClick: (String) -> Unit,
    onExternalLinksClick: (String) -> Unit,
    onItemClick: (String, MediaType) -> Unit,
    onEntityClick: (EntityType, String) -> Unit,
    onTopicNavigate: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var showRelatedBottomSheet by remember { mutableStateOf(false) }

    ConstraintLayout(
        modifier = modifier.fillMaxWidth()
    ) {
        val (descRef, genresRef, charactersRef, relatedRef, screenshotsRef, additionalRef) = createRefs()

        ExpandableText(
            descriptionHtml = animeDetails?.descriptionHtml ?: "No Description",
            modifier = Modifier.constrainAs(descRef) {
                top.linkTo(parent.top)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                width = Dimension.fillToConstraints
            },
            style = MaterialTheme.typography.bodySmall,
            linkColor = MaterialTheme.colorScheme.primary,
            brushColor = MaterialTheme.colorScheme.background.copy(0.8f),
            onEntityClick = { entityType, id ->
                onEntityClick(entityType, id)
            }, onLinkClick = onLinkClick
        )

        LazyRow(
            modifier = Modifier.constrainAs(genresRef) {
                top.linkTo(descRef.bottom, margin = 2.dp)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                width = Dimension.fillToConstraints
            }.padding(vertical = 4.dp),
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
                        characterPoster = characterItem.character.characterShort.poster
                            ?.posterShort?.previewUrl,
                        characterName = characterItem.character.characterShort.name,
                        onClick = { onEntityClick(EntityType.CHARACTER, characterItem.character.characterShort.id) }
                    )
                }
            }
        }

        RelatedSection(
            relatedItems = animeDetails?.related?.map { RelatedMapper.fromAnimeRelated(it) } ?: emptyList(),
            onArrowClick = { showRelatedBottomSheet = true },
            onItemClick = onItemClick,
            modifier = Modifier.constrainAs(relatedRef) {
                top.linkTo(charactersRef.bottom, margin = 12.dp)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                width = Dimension.fillToConstraints
            }
        )

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
                onLinkClick = onLinkClick,
                onSimilarClick = { animeId, title -> onSimilarClick(animeId, title) },
                onExternalLinksClick = onExternalLinksClick,
                onEntityClick = onEntityClick,
                onTopicNavigate = onTopicNavigate,
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
        relatedItems = animeDetails?.related?.map { RelatedMapper.fromAnimeRelated(it) } ?: emptyList(),
        showBottomSheet = showRelatedBottomSheet,
        onItemClick = onItemClick,
        onDismiss = { showRelatedBottomSheet = false }
    )
}

@Composable
fun CharacterCard(
    characterPoster: String?,
    characterName: String,
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
            model = characterPoster, //character.characterShort.poster?.posterShort?.previewUrl,
            size = 96.dp,
            clip = CircleShape,
            contentScale = ContentScale.Crop
        )
        Text(
            text = characterName, //character.characterShort.name,
            overflow = TextOverflow.Ellipsis,
            maxLines = 1,
            style = MaterialTheme.typography.labelMedium.copy(fontSize = 11.sp)
        )
    }
}

@Composable
fun RelatedSection(
    relatedItems: List<RelatedInfo>,
    onItemClick: (String, MediaType) -> Unit,
    onArrowClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Related",
                style = MaterialTheme.typography.titleMedium
            )
            Box(
                modifier = Modifier
                    .size(16.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = relatedItems.size.toString(),
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = MaterialTheme.colorScheme.background
                    )
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            if(relatedItems.size > 3) {
                IconButton(
                    onClick = onArrowClick,
                    modifier = Modifier.size(24.dp),
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = null
                    )
                }
            }
        }
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            relatedItems.take(3).forEach { relatedItem ->
                RelatedItem(
                    relatedInfo = relatedItem,
                    onItemClick = onItemClick
                )
            }
        }
    }
}

@Composable
fun RelatedItem(
    relatedInfo: RelatedInfo,
    onItemClick: (String, MediaType) -> Unit,
    modifier: Modifier = Modifier
) {
    ConstraintLayout(
        modifier = modifier.fillMaxWidth()
    ) {
        val (posterRef, titleRef, infoRef) = createRefs()

        RoundedImage(
            model = relatedInfo.media?.poster?.mainUrl,
            clip = RoundedCornerShape(8.dp),
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(48.dp)
                .clickable { onItemClick(
                    relatedInfo.media?.id ?: "",
                    relatedInfo.media?.mediaType ?: MediaType.ANIME
                ) }
                .constrainAs(posterRef) {
                    start.linkTo(parent.start)
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                }
        )

        Text(
            text = relatedInfo.media?.name ?: "Unknown",
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
            text = "${relatedInfo.media?.kind} ${relatedInfo.relationKind}",
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
private fun ScreenshotSection(
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