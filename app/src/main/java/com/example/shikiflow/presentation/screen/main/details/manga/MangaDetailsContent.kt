package com.example.shikiflow.presentation.screen.main.details.manga

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.shikiflow.R
import com.example.shikiflow.domain.model.comment.EntityType
import com.example.shikiflow.domain.model.media_details.MediaDetails
import com.example.shikiflow.domain.model.track.anime.AnimeShortData.Companion.toShortMangaData
import com.example.shikiflow.domain.model.track.manga.MangaShortData
import com.example.shikiflow.domain.model.tracks.MediaType
import com.example.shikiflow.domain.model.tracks.RateUpdateState
import com.example.shikiflow.domain.model.tracks.SaveUserRate
import com.example.shikiflow.domain.model.tracks.UserRateData.Companion.toUiModel
import com.example.shikiflow.presentation.common.ExpandableText
import com.example.shikiflow.presentation.common.SegmentedProgressBar
import com.example.shikiflow.presentation.common.UserRateBottomSheet
import com.example.shikiflow.presentation.screen.main.details.MediaNavOptions
import com.example.shikiflow.presentation.screen.main.details.RelatedBottomSheet
import com.example.shikiflow.presentation.screen.main.details.common.CharacterCard
import com.example.shikiflow.presentation.screen.main.details.common.RelatedSection
import com.example.shikiflow.utils.Resource
import com.example.shikiflow.utils.WebIntent
import com.example.shikiflow.utils.ignoreHorizontalParentPadding

@Composable
fun MangaDetailsContent(
    userId: Int,
    mangaDetails: MediaDetails,
    mangaDexResource: Resource<List<String>>,
    rateUpdateState: RateUpdateState,
    mediaNavOptions: MediaNavOptions,
    onMangaDexRefreshClick: () -> Unit,
    onSaveUserRate: (Int, SaveUserRate, MangaShortData) -> Unit,
    modifier: Modifier = Modifier
) {
    var rateBottomSheet by remember { mutableStateOf(false) }
    var showRelatedBottomSheet by remember { mutableStateOf(false) }
    val horizontalPadding = 12.dp
    val context = LocalContext.current

    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(
            start = horizontalPadding,
            end = horizontalPadding,
            bottom = 12.dp
        ),
        verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.Top)
    ) {
        item {
            MangaDetailsHeader(
                mangaDetails = mangaDetails,
                mangaDexResource = mangaDexResource,
                horizontalPadding = horizontalPadding,
                onStatusClick = { rateBottomSheet = true },
                onMangaDexNavigateClick = { title ->
                    mediaNavOptions.navigateToMangaRead(
                        mangaDexIds = mangaDexResource.data ?: emptyList(),
                        title = title,
                        completedChapters = mangaDetails.userRate?.progress ?: 0
                    )
                },
                onMangaDexRefreshClick = onMangaDexRefreshClick
            )
        }
        item {
            mangaDetails.descriptionHtml?.let {
                ExpandableText(
                    descriptionHtml = mangaDetails.descriptionHtml,
                    modifier = Modifier.fillMaxWidth(),
                    style = MaterialTheme.typography.bodySmall,
                    linkColor = MaterialTheme.colorScheme.primary,
                    brushColor = MaterialTheme.colorScheme.background.copy(0.8f),
                    onEntityClick = { entityType, id ->
                        mediaNavOptions.navigateByEntity(entityType, id)
                    },
                    onLinkClick = { url ->
                        WebIntent.openUrlCustomTab(context, url)
                    }
                )
            }
        }
        if(mangaDetails.characters.isNotEmpty()) {
            item {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = stringResource(R.string.details_characters),
                            style = MaterialTheme.typography.titleMedium
                        )
                        IconButton(
                            onClick = {
                                mediaNavOptions.navigateToMediaCharacters(
                                    mediaId = mangaDetails.id,
                                    mediaTitle = mangaDetails.title,
                                    mediaType = MediaType.MANGA
                                )
                            },
                            modifier = Modifier.size(24.dp),
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                contentDescription = null
                            )
                        }
                    }
                    LazyRow(
                        modifier = Modifier.ignoreHorizontalParentPadding(horizontalPadding)
                            .fillMaxWidth(),
                        contentPadding = PaddingValues(horizontal = horizontalPadding),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(mangaDetails.characters) { characterItem ->
                            CharacterCard(
                                characterPoster = characterItem.imageUrl,
                                characterName = characterItem.fullName,
                                onClick = {
                                    mediaNavOptions.navigateByEntity(EntityType.CHARACTER, characterItem.id)
                                },
                                modifier = Modifier.width(96.dp)
                            )
                        }
                    }
                }
            }
        }
        item {
            Box(
                modifier = Modifier.fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.surface)
            ) {
                SegmentedProgressBar(
                    mediaType = MediaType.MANGA,
                    groupedData = mangaDetails.statusesStats,
                    totalCount = mangaDetails.statusesStats.size,
                    modifier = Modifier.padding(12.dp),
                    itemShape = RoundedCornerShape(3.dp),
                    rowHeight = 16.dp,
                    rowShape = RoundedCornerShape(4.dp)
                )
            }
        }
        if(mangaDetails.relatedMedia.isNotEmpty()) {
            item {
                RelatedSection(
                    relatedItems = mangaDetails.relatedMedia,
                    onItemClick = { id, mediaType ->
                        if (mediaType == MediaType.ANIME) {
                            mediaNavOptions.navigateToAnimeDetails(id)
                        } else mediaNavOptions.navigateToMangaDetails(id)
                    },
                    onArrowClick = { showRelatedBottomSheet = true }
                )
            }
        }
    }

    if(rateBottomSheet) {
        UserRateBottomSheet(
            userRate = mangaDetails.toUiModel(),
            rateUpdateState = rateUpdateState,
            onDismiss = { rateBottomSheet = false },
            onSave = { save ->
                onSaveUserRate(userId, save, mangaDetails.toShortMangaData())
            }
        )
    }
    if(showRelatedBottomSheet) {
        RelatedBottomSheet(
            relatedItems = mangaDetails.relatedMedia,
            onItemClick = { id, mediaType ->
                if (mediaType == MediaType.ANIME) {
                    mediaNavOptions.navigateToAnimeDetails(id)
                } else mediaNavOptions.navigateToMangaDetails(id)
            },
            onDismiss = { showRelatedBottomSheet = false }
        )
    }
}