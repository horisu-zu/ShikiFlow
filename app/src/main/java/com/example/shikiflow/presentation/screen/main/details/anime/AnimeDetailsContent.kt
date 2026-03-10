package com.example.shikiflow.presentation.screen.main.details.anime

import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.gestures.snapping.SnapPosition
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.shikiflow.R
import com.example.shikiflow.domain.model.auth.AuthType
import com.example.shikiflow.domain.model.comment.CommentsScreenMode
import com.example.shikiflow.domain.model.comment.EntityType
import com.example.shikiflow.domain.model.media_details.MediaDetails
import com.example.shikiflow.domain.model.media_details.MediaStatus
import com.example.shikiflow.domain.model.track.anime.AnimeShortData
import com.example.shikiflow.domain.model.track.anime.AnimeShortData.Companion.toShortAnimeData
import com.example.shikiflow.domain.model.tracks.MediaType
import com.example.shikiflow.domain.model.tracks.RateUpdateState
import com.example.shikiflow.domain.model.tracks.SaveUserRate
import com.example.shikiflow.domain.model.tracks.UserRateData.Companion.toUiModel
import com.example.shikiflow.presentation.common.CardItem
import com.example.shikiflow.presentation.common.ExpandableText
import com.example.shikiflow.presentation.common.SnapFlingLazyRow
import com.example.shikiflow.presentation.common.UserRateBottomSheet
import com.example.shikiflow.presentation.screen.main.details.MediaNavOptions
import com.example.shikiflow.presentation.screen.main.details.RelatedBottomSheet
import com.example.shikiflow.presentation.screen.main.details.character.PaginatedListNavigateIcon
import com.example.shikiflow.presentation.screen.main.details.common.CharacterCard
import com.example.shikiflow.presentation.screen.main.details.common.RelatedSection
import com.example.shikiflow.presentation.screen.main.details.common.StaffSection
import com.example.shikiflow.presentation.screen.main.details.common.comment.CommentSection
import com.example.shikiflow.utils.Converter.isHTMLStringBlank
import com.example.shikiflow.utils.WebIntent
import com.example.shikiflow.utils.ignoreHorizontalParentPadding

@Composable
fun AnimeDetailsContent(
    userId: Int,
    currentAuthType: AuthType,
    animeDetails: MediaDetails,
    rateUpdateState: RateUpdateState,
    sharedTransitionScope: SharedTransitionScope,
    selectedScreenshotIndex: Int?,
    onScreenshotClick: (Int) -> Unit,
    onSaveUserRate: (Int, SaveUserRate, AnimeShortData) -> Unit,
    mediaNavOptions: MediaNavOptions,
    modifier: Modifier = Modifier
) {
    var rateBottomSheet by remember { mutableStateOf(false) }
    var showRelatedBottomSheet by remember { mutableStateOf(false) }
    val horizontalPadding = 12.dp
    val context = LocalContext.current
    val density = LocalDensity.current

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
            AnimeDetailsTitle(
                animeDetails = animeDetails,
                authType = currentAuthType,
                horizontalPadding = horizontalPadding,
                onStatusClick = { rateBottomSheet = true },
                onPlayClick = { title, id, completedEpisodes ->
                    mediaNavOptions.navigateToAnimeWatch(title, id, completedEpisodes)
                }
            )
        }
        if(animeDetails.descriptionHtml?.isHTMLStringBlank() != true) {
            item {
                ExpandableText(
                    htmlText = animeDetails.descriptionHtml ?: "",
                    authType = currentAuthType,
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
        item {
            SnapFlingLazyRow(
                snapPosition = SnapPosition.Start,
                contentPadding = PaddingValues(horizontal = horizontalPadding),
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.Start),
                modifier = Modifier.ignoreHorizontalParentPadding(horizontalPadding).fillMaxWidth()
            ) {
                items(animeDetails.genres) { genreItem ->
                    CardItem(
                        item = genreItem
                    )
                }
            }
        }
        if(animeDetails.characters.entries.isNotEmpty()) {
            item {
                var maxCardHeight by remember { mutableIntStateOf(0) }
                val characterCardWidth = 96.dp

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
                                    mediaId = animeDetails.id,
                                    mediaTitle = animeDetails.title,
                                    mediaType = MediaType.ANIME
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
                    SnapFlingLazyRow(
                        snapPosition = SnapPosition.Start,
                        modifier = Modifier
                            .ignoreHorizontalParentPadding(horizontalPadding)
                            .fillMaxWidth(),
                        contentPadding = PaddingValues(horizontal = horizontalPadding),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(animeDetails.characters.entries) { characterItem ->
                            CharacterCard(
                                characterPoster = characterItem.imageUrl,
                                characterName = characterItem.fullName,
                                onClick = { mediaNavOptions.navigateByEntity(EntityType.CHARACTER, characterItem.id) },
                                modifier = Modifier.width(characterCardWidth)
                                    .onSizeChanged { size ->
                                        maxCardHeight = size.height
                                    }
                            )
                        }
                        if(animeDetails.characters.hasNextPage) {
                            item {
                                PaginatedListNavigateIcon(
                                    onNavigate = {
                                        mediaNavOptions.navigateToMediaCharacters(
                                            mediaId = animeDetails.id,
                                            mediaTitle = animeDetails.title,
                                            mediaType = MediaType.ANIME
                                        )
                                    },
                                    modifier = Modifier
                                        .height(
                                            height = with(density) { maxCardHeight.toDp() }
                                        )
                                        .width(characterCardWidth)
                                        .clip(CircleShape)
                                )
                            }
                        }
                    }
                }
            }
        }
        if(animeDetails.relatedMedia.isNotEmpty()) {
            item {
                RelatedSection(
                    relatedItems = animeDetails.relatedMedia,
                    onArrowClick = { showRelatedBottomSheet = true },
                    onItemClick = { id, mediaType ->
                        if (mediaType == MediaType.ANIME) {
                            mediaNavOptions.navigateToAnimeDetails(id)
                        } else mediaNavOptions.navigateToMangaDetails(id)
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
        if(animeDetails.staffList.isNotEmpty()) {
            item {
                StaffSection(
                    staffShortList = animeDetails.staffList,
                    onMediaStaffClick = {
                        mediaNavOptions.navigateToMediaStaff(animeDetails.id, MediaType.MANGA)
                    },
                    onStaffClick = { staffId ->
                        mediaNavOptions.navigateToStaff(staffId)
                    },
                    horizontalPadding = horizontalPadding,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
        if(animeDetails.screenshots.isNotEmpty()) {
            item {
                ScreenshotSection(
                    screenshots = animeDetails.screenshots,
                    selectedIndex = selectedScreenshotIndex,
                    onScreenshotClick = onScreenshotClick,
                    sharedTransitionScope = sharedTransitionScope,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
        item {
            AnimeShortInfoSection(
                animeDetails = animeDetails,
                onStudioClick = { studioId, studioName ->
                    mediaNavOptions.navigateToStudio(studioId, studioName)
                },
                modifier = Modifier.fillMaxWidth()
            )
        }
        item {
            HorizontalDivider()
        }
        item {
            MediaDetailsNavComponent(
                authType = currentAuthType,
                onThreadsClick = {
                    mediaNavOptions.navigateToThreads(mediaId = animeDetails.id)
                },
                onSimilarClick = {
                    mediaNavOptions.navigateToSimilarPage(animeDetails.id, animeDetails.title, MediaType.ANIME)
                },
                onExternalLinksClick = {
                    mediaNavOptions.navigateToLinksPage(animeDetails.id, MediaType.ANIME)
                }
            )
        }
        item {
            HorizontalDivider()
        }
        item {
            MediaStatsComponent(
                mediaType = animeDetails.mediaType,
                isAnnounced = animeDetails.status == MediaStatus.ANNOUNCED,
                titleScore = animeDetails.score,
                scoreStats = animeDetails.scoreStats,
                statusesStats = animeDetails.statusesStats
            )
        }
        animeDetails.threadId?.let { threadId ->
            item {
                CommentSection(
                    topicId = threadId,
                    onEntityClick = { entityType, id ->
                        mediaNavOptions.navigateByEntity(entityType, id)
                    },
                    onLinkClick = { url ->
                        WebIntent.openUrlCustomTab(context, url)
                    },
                    onTopicNavigate = { topicId ->
                        mediaNavOptions.navigateToComments(
                            screenMode = CommentsScreenMode.TOPIC,
                            id = topicId
                        )
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
    if(rateBottomSheet) {
        UserRateBottomSheet(
            userRate = animeDetails.toUiModel(),
            rateUpdateState = rateUpdateState,
            onDismiss = { rateBottomSheet = false },
            onSave = { save ->
                onSaveUserRate(userId, save, animeDetails.toShortAnimeData())
            }
        )
    }
    if(showRelatedBottomSheet) {
        RelatedBottomSheet(
            relatedItems = animeDetails.relatedMedia,
            onItemClick = { id, mediaType ->
                if (mediaType == MediaType.ANIME) {
                    mediaNavOptions.navigateToAnimeDetails(id)
                } else mediaNavOptions.navigateToMangaDetails(id)
            },
            onDismiss = { showRelatedBottomSheet = false }
        )
    }
}