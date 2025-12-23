package com.example.shikiflow.presentation.screen.main.details.anime

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.graphql.AnimeDetailsQuery
import com.example.shikiflow.R
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
import com.example.shikiflow.utils.ignoreHorizontalParentPadding

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun AnimeDetailsDesc(
    animeDetails: AnimeDetailsQuery.Anime,
    horizontalPadding: Dp,
    selectedScreenshotIndex: Int?,
    sharedTransitionScope: SharedTransitionScope,
    isRefreshing: Boolean,
    onStudioClick: (String, String) -> Unit,
    onSimilarClick: (String, String) -> Unit,
    onLinkClick: (String) -> Unit,
    onExternalLinksClick: (String) -> Unit,
    onItemClick: (String, MediaType) -> Unit,
    onEntityClick: (EntityType, String) -> Unit,
    onTopicNavigate: (String) -> Unit,
    onScreenshotClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var showRelatedBottomSheet by remember { mutableStateOf(false) }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.Top)
    ) {
        if(!animeDetails.description.isNullOrEmpty()) {
            ExpandableText(
                descriptionHtml = animeDetails.descriptionHtml ?: "",
                modifier = Modifier.fillMaxWidth(),
                style = MaterialTheme.typography.bodySmall,
                linkColor = MaterialTheme.colorScheme.primary,
                brushColor = MaterialTheme.colorScheme.background.copy(0.8f),
                onEntityClick = { entityType, id ->
                    onEntityClick(entityType, id)
                }, onLinkClick = onLinkClick
            )
        }

        if(!animeDetails.genres.isNullOrEmpty()) {
            LazyRow(
                modifier = Modifier.ignoreHorizontalParentPadding(horizontalPadding).fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = horizontalPadding),
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.Start)
            ) {
                items(animeDetails.genres) { genreItem ->
                    CardItem(
                        item = genreItem.name
                    )
                }
            }
        }

        if(!animeDetails.characterRoles.isNullOrEmpty()) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = stringResource(R.string.details_characters),
                    style = MaterialTheme.typography.titleMedium
                )
                LazyRow(
                    modifier = Modifier.ignoreHorizontalParentPadding(horizontalPadding)
                        .fillMaxWidth(),
                    contentPadding = PaddingValues(horizontal = horizontalPadding),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(animeDetails.characterRoles) { characterItem ->
                        CharacterCard(
                            characterPoster = characterItem.character.characterShort.poster
                                ?.posterShort?.previewUrl,
                            characterName = characterItem.character.characterShort.name,
                            onClick = { onEntityClick(EntityType.CHARACTER, characterItem.character.characterShort.id) },
                            modifier = Modifier.width(96.dp)
                        )
                    }
                }
            }
        }

        if(!animeDetails.related.isNullOrEmpty()) {
            RelatedSection(
                relatedItems = animeDetails.related.map { RelatedMapper.fromAnimeRelated(it) },
                onArrowClick = { showRelatedBottomSheet = true },
                onItemClick = onItemClick,
                modifier = Modifier.fillMaxWidth()
            )
        }

        if(animeDetails.screenshots.isNotEmpty()) {
            ScreenshotSection(
                screenshots = animeDetails.screenshots,
                selectedIndex = selectedScreenshotIndex,
                onScreenshotClick = onScreenshotClick,
                sharedTransitionScope = sharedTransitionScope,
                modifier = Modifier.fillMaxWidth()
            )
        }

        AnimeDetailsInfo(
            animeDetails = animeDetails,
            isRefreshing = isRefreshing,
            onStudioClick = onStudioClick,
            onLinkClick = onLinkClick,
            onSimilarClick = { animeId, title -> onSimilarClick(animeId, title) },
            onExternalLinksClick = onExternalLinksClick,
            onEntityClick = onEntityClick,
            onTopicNavigate = onTopicNavigate
        )
    }

    if(showRelatedBottomSheet) {
        RelatedBottomSheet(
            relatedItems = animeDetails.related?.map { RelatedMapper.fromAnimeRelated(it) } ?: emptyList(),
            onItemClick = onItemClick,
            onDismiss = { showRelatedBottomSheet = false }
        )
    }
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
            .clip(RoundedCornerShape(8.dp))
            .clickable { onClick() },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        RoundedImage(
            model = characterPoster, //character.characterShort.poster?.posterShort?.previewUrl,
            modifier = Modifier.fillMaxWidth(),
            clip = CircleShape,
            contentScale = ContentScale.Crop
        )
        Text(
            text = characterName, //character.characterShort.name,
            overflow = TextOverflow.Ellipsis,
            maxLines = 1,
            style = MaterialTheme.typography.labelSmall
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
                text = stringResource(R.string.details_related),
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
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.Start),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RoundedImage(
            model = relatedInfo.media?.poster?.mainUrl,
            clip = RoundedCornerShape(8.dp),
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(48.dp)
                .clickable {
                    onItemClick(
                        relatedInfo.media?.id ?: "",
                        relatedInfo.media?.mediaType ?: MediaType.ANIME
                    )
                }
        )
        relatedInfo.media?.name?.let { title ->
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = title,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.labelMedium
                )
                relatedInfo.media?.let { media ->
                    Text(
                        text = buildString {
                            append(stringResource(id = media.kind))
                            append(" ∙ ")
                            append(stringResource(id = relatedInfo.relationKind))
                        },
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                        )
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun ScreenshotSection(
    screenshots: List<AnimeDetailsQuery.Screenshot>,
    selectedIndex: Int?,
    sharedTransitionScope: SharedTransitionScope,
    onScreenshotClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
    horizontalPadding: Dp = 12.dp
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = stringResource(R.string.anime_details_screenshots),
            style = MaterialTheme.typography.titleMedium
        )
        LazyRow(
            modifier = Modifier.ignoreHorizontalParentPadding(horizontalPadding)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(horizontal = horizontalPadding)
        ) {
            itemsIndexed(screenshots) { index, screenshot ->
                AnimatedVisibility(
                    visible = index != selectedIndex,
                    enter = fadeIn(),
                    exit = fadeOut(),
                    modifier = Modifier.animateItem()
                ) {
                    with(sharedTransitionScope) {
                        BaseImage(
                            model = screenshot.originalUrl,
                            modifier = Modifier
                                .clickable { onScreenshotClick(index) }
                                .sharedElement(
                                    sharedContentState = rememberSharedContentState(key = screenshot.originalUrl),
                                    animatedVisibilityScope = this@AnimatedVisibility
                                ),
                            imageType = ImageType.Screenshot()
                        )
                    }
                }
            }
        }
    }
}