package com.example.shikiflow.presentation.screen.main.details.manga

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.graphql.MangaDetailsQuery
import com.example.shikiflow.R
import com.example.shikiflow.domain.model.mapper.RelatedMapper
import com.example.shikiflow.domain.model.mapper.UserRateMapper.Companion.mapStatusToString
import com.example.shikiflow.domain.model.tracks.MediaType
import com.example.shikiflow.presentation.common.ExpandableText
import com.example.shikiflow.presentation.common.SegmentedProgressBar
import com.example.shikiflow.presentation.screen.main.details.RelatedBottomSheet
import com.example.shikiflow.presentation.screen.main.details.anime.CharacterCard
import com.example.shikiflow.presentation.screen.main.details.anime.RelatedSection
import com.example.shikiflow.presentation.screen.main.details.common.CommentSection
import com.example.shikiflow.utils.Converter.EntityType

@Composable
fun MangaDetailsDesc(
    mangaDetails: MangaDetailsQuery.Manga?,
    onItemClick: (String, MediaType) -> Unit,
    onEntityClick: (EntityType, String) -> Unit,
    onLinkClick: (String) -> Unit,
    onTopicNavigate: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var showRelatedBottomSheet by remember { mutableStateOf(false) }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.Top)
    ) {
        mangaDetails?.descriptionHtml?.let { descriptionHtml ->
            ExpandableText(
                descriptionHtml = descriptionHtml,
                modifier = Modifier.fillMaxWidth(),
                style = MaterialTheme.typography.bodySmall,
                linkColor = MaterialTheme.colorScheme.primary,
                brushColor = MaterialTheme.colorScheme.background.copy(0.8f),
                onEntityClick = { entityType, id ->
                    onEntityClick(entityType, id)
                }, onLinkClick = onLinkClick
            )
        }

        mangaDetails?.characterRoles?.let { characterRoles ->
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = stringResource(R.string.details_characters),
                    style = MaterialTheme.typography.titleMedium
                )
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(characterRoles) { characterItem ->
                        CharacterCard(
                            characterPoster = characterItem.character.characterShort.poster
                                ?.posterShort?.previewUrl,
                            characterName = characterItem.character.characterShort.name,
                            onClick = { onEntityClick(EntityType.CHARACTER, characterItem.character.characterShort.id) }
                        )
                    }
                }
            }
        }
        SegmentedProgressBar(
            groupedData = mangaDetails?.statusesStats?.associate {
                mapStatusToString(it.status, MediaType.MANGA) to it.count
            } ?: emptyMap(),
            totalCount = mangaDetails?.statusesStats?.size ?: 0,
            modifier = Modifier.padding(top = 4.dp),
            itemShape = RoundedCornerShape(3.dp),
            rowHeight = 16.dp,
            rowShape = RoundedCornerShape(4.dp)
        )

        if(mangaDetails?.related != null && mangaDetails.related.isNotEmpty()) {
            RelatedSection(
                relatedItems = mangaDetails.related.map { RelatedMapper.fromMangaRelated(it) },
                onItemClick = onItemClick,
                onArrowClick = { showRelatedBottomSheet = true },
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        mangaDetails?.topic?.id?.let { topicId ->
            CommentSection(
                topicId = topicId,
                onEntityClick = onEntityClick,
                onTopicNavigate = onTopicNavigate,
                onLinkClick = onLinkClick
            )
        }
    }

    RelatedBottomSheet(
        relatedItems = mangaDetails?.related?.map { RelatedMapper.fromMangaRelated(it) } ?: emptyList(),
        showBottomSheet = showRelatedBottomSheet,
        onItemClick = onItemClick,
        onDismiss = { showRelatedBottomSheet = false }
    )
}