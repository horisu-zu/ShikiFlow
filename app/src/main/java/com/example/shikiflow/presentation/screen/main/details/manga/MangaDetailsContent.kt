package com.example.shikiflow.presentation.screen.main.details.manga

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
import com.example.shikiflow.domain.model.track.anime.AnimeShortData.Companion.toShortMangaData
import com.example.shikiflow.domain.model.track.manga.MangaShortData
import com.example.shikiflow.domain.model.tracks.MediaType
import com.example.shikiflow.domain.model.tracks.RateUpdateState
import com.example.shikiflow.domain.model.tracks.SaveUserRate
import com.example.shikiflow.domain.model.tracks.UserRateData.Companion.toUiModel
import com.example.shikiflow.presentation.common.ExpandableText
import com.example.shikiflow.presentation.common.SnapFlingLazyRow
import com.example.shikiflow.presentation.common.UserRateBottomSheet
import com.example.shikiflow.presentation.screen.main.details.MediaNavOptions
import com.example.shikiflow.presentation.screen.main.details.RelatedBottomSheet
import com.example.shikiflow.presentation.screen.main.details.anime.MediaDetailsNavComponent
import com.example.shikiflow.presentation.screen.main.details.anime.MediaStatsComponent
import com.example.shikiflow.presentation.screen.main.details.character.PaginatedListNavigateIcon
import com.example.shikiflow.presentation.screen.main.details.common.CharacterCard
import com.example.shikiflow.presentation.screen.main.details.common.RelatedSection
import com.example.shikiflow.presentation.screen.main.details.common.StaffSection
import com.example.shikiflow.presentation.screen.main.details.common.comment.CommentSection
import com.example.shikiflow.utils.Resource
import com.example.shikiflow.utils.WebIntent
import com.example.shikiflow.utils.ignoreHorizontalParentPadding

@Composable
fun MangaDetailsContent(
    userId: Int,
    authType: AuthType,
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
            MangaDetailsHeader(
                mangaDetails = mangaDetails,
                mangaDexResource = mangaDexResource,
                horizontalPadding = horizontalPadding,
                onStatusClick = { rateBottomSheet = true },
                onMangaDexIconClick = {
                    if(!mangaDexResource.data.isNullOrEmpty()) {
                        mediaNavOptions.navigateToMangaRead(
                            mangaDexIds = mangaDexResource.data,
                            title = mangaDetails.title,
                            completedChapters = mangaDetails.userRate?.progress ?: 0
                        )
                    } else if(mangaDexResource is Resource.Error) {
                        onMangaDexRefreshClick()
                    }
                }
            )
        }
        item {
            mangaDetails.descriptionHtml?.let {
                ExpandableText(
                    htmlText = mangaDetails.descriptionHtml,
                    authType = authType,
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
        if(mangaDetails.characters.entries.isNotEmpty()) {
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
                    SnapFlingLazyRow(
                        modifier = Modifier
                            .ignoreHorizontalParentPadding(horizontalPadding)
                            .fillMaxWidth(),
                        contentPadding = PaddingValues(horizontal = horizontalPadding),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(mangaDetails.characters.entries) { characterItem ->
                            CharacterCard(
                                characterPoster = characterItem.imageUrl,
                                characterName = characterItem.fullName,
                                onClick = {
                                    mediaNavOptions.navigateByEntity(EntityType.CHARACTER, characterItem.id)
                                },
                                modifier = Modifier.width(characterCardWidth)
                                    .onSizeChanged { size ->
                                        maxCardHeight = size.height
                                    }
                            )
                        }
                        if(mangaDetails.characters.hasNextPage) {
                            item {
                                PaginatedListNavigateIcon(
                                    onNavigate = {
                                        mediaNavOptions.navigateToMediaCharacters(
                                            mediaId = mangaDetails.id,
                                            mediaTitle = mangaDetails.title,
                                            mediaType = MediaType.MANGA
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
        if(mangaDetails.staffList.isNotEmpty()) {
            item {
                StaffSection(
                    staffShortList = mangaDetails.staffList,
                    onMediaStaffClick = {
                        mediaNavOptions.navigateToMediaStaff(mangaDetails.id, MediaType.MANGA)
                    },
                    onStaffClick = { staffId ->
                        mediaNavOptions.navigateToStaff(staffId)
                    },
                    horizontalPadding = horizontalPadding
                )
            }
        }
        item {
            HorizontalDivider()
        }
        item {
            MediaDetailsNavComponent(
                authType = authType,
                mediaType = mangaDetails.mediaType,
                onThreadsClick = {
                    mediaNavOptions.navigateToThreads(mangaDetails.id)
                },
                onStaffClick = {
                    mediaNavOptions.navigateToMediaStaff(mangaDetails.id, mangaDetails.mediaType)
                },
                onSimilarClick = {
                    mediaNavOptions.navigateToSimilarPage(mangaDetails.id, mangaDetails.title, mangaDetails.mediaType)
                },
                onExternalLinksClick = {
                    mediaNavOptions.navigateToLinksPage(mangaDetails.id, mangaDetails.mediaType)
                }
            )
        }
        item {
            HorizontalDivider()
        }
        item {
            MediaStatsComponent(
                mediaType = mangaDetails.mediaType,
                isAnnounced = mangaDetails.status == MediaStatus.ANNOUNCED,
                titleScore = mangaDetails.score,
                scoreStats = mangaDetails.scoreStats,
                statusesStats = mangaDetails.statusesStats
            )
        }
        mangaDetails.threadId?.let { threadId ->
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