package com.example.shikiflow.presentation.screen.main.details.manga

import androidx.compose.foundation.gestures.snapping.SnapPosition
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.shikiflow.R
import com.example.shikiflow.data.mapper.local.MediaShortMapper.toShortData
import com.example.shikiflow.domain.model.auth.AuthType
import com.example.shikiflow.domain.model.comment.CommentsScreenMode
import com.example.shikiflow.domain.model.comment.EntityType
import com.example.shikiflow.domain.model.common.ScoreFormat
import com.example.shikiflow.domain.model.media_details.MediaDetails
import com.example.shikiflow.domain.model.media_details.MediaStatus
import com.example.shikiflow.domain.model.media_details.MediaTitle.Companion.preferred
import com.example.shikiflow.domain.model.staff.StaffName.Companion.preferred
import com.example.shikiflow.domain.model.track.media.MediaShortData
import com.example.shikiflow.domain.model.track.media.MediaUserTrack
import com.example.shikiflow.domain.model.tracks.MediaType
import com.example.shikiflow.domain.model.tracks.RateUpdateState
import com.example.shikiflow.domain.model.tracks.SaveUserRate
import com.example.shikiflow.domain.model.tracks.UserRateData.Companion.toUiModel
import com.example.shikiflow.presentation.common.CardItem
import com.example.shikiflow.presentation.common.ExpandableText
import com.example.shikiflow.presentation.common.SnapFlingLazyRow
import com.example.shikiflow.presentation.common.TextWithDivider
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
import com.example.shikiflow.presentation.screen.main.details.common.followings.MediaFollowingsSection
import com.example.shikiflow.presentation.screen.main.details.common.review.ReviewsSection
import com.example.shikiflow.presentation.viewmodel.manga.details.MangaDexUiState
import com.example.shikiflow.presentation.common.ignoreHorizontalParentPadding
import com.example.shikiflow.presentation.common.image.ImageType
import com.example.shikiflow.presentation.common.mappers.GenreMapper.displayValue
import com.example.shikiflow.presentation.screen.main.LocalTitleTypeController
import com.example.shikiflow.presentation.screen.main.details.common.MediaTagItem
import com.example.shikiflow.utils.Converter.isHTMLStringBlank

@Composable
fun MangaDetailsContent(
    authType: AuthType,
    mangaDetails: MediaDetails,
    userRate: MediaUserTrack?,
    mangaDexUiState: MangaDexUiState,
    rateUpdateState: RateUpdateState,
    scoreFormat: ScoreFormat,
    mediaNavOptions: MediaNavOptions,
    onMangaDexRefreshClick: () -> Unit,
    onSaveUserRate: (SaveUserRate, MediaShortData) -> Unit,
    onDeleteUserRate: (Int) -> Unit,
    onToggleFavorite: () -> Unit,
    modifier: Modifier = Modifier
) {
    var rateBottomSheet by remember { mutableStateOf(false) }
    var showRelatedBottomSheet by remember { mutableStateOf(false) }

    val horizontalPadding = 12.dp
    val titleType = LocalTitleTypeController.current

    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(
            start = horizontalPadding,
            end = horizontalPadding,
            bottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
        ),
        verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.Top)
    ) {
        item {
            MangaDetailsHeader(
                mangaDetails = mangaDetails,
                userRate = userRate,
                mangaDexUiState = mangaDexUiState,
                titleType = titleType,
                scoreFormat = scoreFormat,
                horizontalPadding = horizontalPadding,
                onStatusClick = { rateBottomSheet = true },
                onMangaDexIconClick = {
                    if(mangaDexUiState.mangaDexIds.isNotEmpty()) {
                        mediaNavOptions.navigateToMangaRead(
                            mangaDexIds = mangaDexUiState.mangaDexIds,
                            trackerMangaId = mangaDetails.id,
                            malId = mangaDetails.malId,
                            title = mangaDetails.title.preferred(titleType),
                            completedChapters = userRate?.progress ?: 0
                        )
                    } else if(mangaDexUiState.errorMessage != null) {
                        onMangaDexRefreshClick()
                    }
                },
                onToggleFavorite = onToggleFavorite
            )
        }

        if(!mangaDetails.descriptionHtml.isHTMLStringBlank()) {
            item {
                mangaDetails.descriptionHtml?.let {
                    ExpandableText(
                        htmlText = mangaDetails.descriptionHtml,
                        modifier = Modifier.fillMaxWidth(),
                        style = MaterialTheme.typography.bodySmall,
                        linkColor = MaterialTheme.colorScheme.primary,
                        brushColor = MaterialTheme.colorScheme.background.copy(0.8f),
                        onEntityClick = { entityType, id ->
                            mediaNavOptions.navigateByEntity(entityType, id)
                        }
                    )
                }
            }
        }

        if(mangaDetails.genres.isNotEmpty()) {
            item {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TextWithDivider(
                        text = stringResource(R.string.user_stats_section_genres)
                    )
                    SnapFlingLazyRow(
                        snapPosition = SnapPosition.Start,
                        contentPadding = PaddingValues(horizontal = horizontalPadding),
                        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.Start),
                        modifier = Modifier
                            .ignoreHorizontalParentPadding(horizontalPadding)
                            .fillMaxWidth()
                    ) {
                        items(mangaDetails.genres) { genreItem ->
                            CardItem(
                                item = stringResource(genreItem.displayValue())
                            )
                        }
                    }
                }
            }
        }

        if(mangaDetails.tags.isNotEmpty()) {
            item {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TextWithDivider(
                        text = stringResource(R.string.user_stats_section_tags)
                    )
                    SnapFlingLazyRow(
                        snapPosition = SnapPosition.Start,
                        contentPadding = PaddingValues(horizontal = horizontalPadding),
                        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.Start),
                        modifier = Modifier
                            .ignoreHorizontalParentPadding(horizontalPadding)
                            .fillMaxWidth()
                    ) {
                        items(mangaDetails.tags) { tagItem ->
                            MediaTagItem(tagItem)
                        }
                    }
                }
            }
        }

        if(mangaDetails.characters.entries.isNotEmpty()) {
            item {
                val clipPercent = 16
                val imageType = ImageType.Custom(
                    width = 96.dp,
                    aspectRatio = 2f / 2.85f,
                    shape = RoundedCornerShape(clipPercent)
                )

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        TextWithDivider(
                            text = stringResource(R.string.details_characters)
                        )
                        IconButton(
                            onClick = {
                                mediaNavOptions.navigateToMediaCharacters(
                                    mediaId = mangaDetails.id,
                                    mediaTitle = mangaDetails.title.preferred(titleType),
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
                                characterName = characterItem.fullName.preferred(titleType),
                                onClick = {
                                    mediaNavOptions.navigateByEntity(EntityType.CHARACTER, characterItem.id)
                                },
                                clipPercent = clipPercent,
                                imageType = imageType
                            )
                        }
                        if(mangaDetails.characters.hasNextPage) {
                            item {
                                PaginatedListNavigateIcon(
                                    onNavigate = {
                                        mediaNavOptions.navigateToMediaCharacters(
                                            mediaId = mangaDetails.id,
                                            mediaTitle = mangaDetails.title.preferred(titleType),
                                            mediaType = MediaType.MANGA
                                        )
                                    },
                                    modifier = Modifier
                                        .width(imageType.width)
                                        .aspectRatio(imageType.aspectRatio)
                                        .clip(imageType.shape)
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
                    preferredTitleType = titleType,
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
                    preferredTitleType = titleType,
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
                onThreadsClick = {
                    mediaNavOptions.navigateToThreads(mangaDetails.id)
                },
                onSimilarClick = {
                    mediaNavOptions.navigateToSimilarPage(
                        id = mangaDetails.id,
                        title = mangaDetails.title.preferred(titleType),
                        mediaType = mangaDetails.mediaType
                    )
                },
                onExternalLinksClick = {
                    mediaNavOptions.navigateToLinksPage(mangaDetails.id, mangaDetails.mediaType)
                }
            )
        }
        item {
            HorizontalDivider()
        }
        if(mangaDetails.reviews.entries.isNotEmpty()) {
            item {
                ReviewsSection(
                    reviewsList = mangaDetails.reviews,
                    onMoreClick = {
                        mediaNavOptions.navigateToMediaReviews(mangaDetails.id, MediaType.MANGA)
                    },
                    onReviewClick = { reviewId ->
                        mediaNavOptions.navigateToReview(reviewId)
                    },
                    horizontalPadding = horizontalPadding
                )
            }
        }
        if(mangaDetails.mediaFollowings.entries.isNotEmpty()) {
            item {
                MediaFollowingsSection(
                    mediaFollowings = mangaDetails.mediaFollowings,
                    episodesCount = mangaDetails.totalCount,
                    onUserClick = { user ->
                        mediaNavOptions.navigateToUserProfile(user)
                    },
                    onMoreClick = {
                        mediaNavOptions.navigateToMediaFollowings(mangaDetails.id, mangaDetails.totalCount)
                    }
                )
            }
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
                    onTopicNavigate = { topicId ->
                        mediaNavOptions.navigateToComments(
                            screenMode = CommentsScreenMode.TOPIC,
                            id = topicId
                        )
                    },
                    onUserClick = { user ->
                        mediaNavOptions.navigateToUserProfile(user)
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }

    if(rateBottomSheet) {
        UserRateBottomSheet(
            userRate = mangaDetails.toUiModel(userRate),
            preferredTitleType = titleType,
            rateUpdateState = rateUpdateState,
            scoreFormat = scoreFormat,
            onDismiss = { rateBottomSheet = false },
            onSave = { save ->
                onSaveUserRate(save, mangaDetails.toShortData())
            },
            onDelete = { entryId -> onDeleteUserRate(entryId) }
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