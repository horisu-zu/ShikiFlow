package com.example.shikiflow.presentation.screen.main.details.manga

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.graphql.MangaDetailsQuery
import com.example.shikiflow.R
import com.example.shikiflow.domain.model.mapper.RelatedMapper
import com.example.shikiflow.domain.model.mapper.UserRateMapper.Companion.simpleMapUserRateStatusToString
import com.example.shikiflow.domain.model.tracks.MediaType
import com.example.shikiflow.presentation.common.ExpandableText
import com.example.shikiflow.presentation.common.SegmentedProgressBar
import com.example.shikiflow.presentation.common.image.BaseImage
import com.example.shikiflow.presentation.screen.main.details.RelatedBottomSheet
import com.example.shikiflow.presentation.screen.main.details.anime.CharacterCard
import com.example.shikiflow.presentation.screen.main.details.anime.RelatedSection
import com.example.shikiflow.presentation.screen.main.details.common.CommentSection
import com.example.shikiflow.utils.Converter.EntityType
import com.example.shikiflow.utils.ignoreHorizontalParentPadding

@Composable
fun MangaDetailsDesc(
    mangaDetails: MangaDetailsQuery.Manga,
    horizontalPadding: Dp,
    isRefreshing: Boolean,
    onItemClick: (String, MediaType) -> Unit,
    onEntityClick: (EntityType, String) -> Unit,
    onLinkClick: (String) -> Unit,
    onTopicNavigate: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var showRelatedBottomSheet by remember { mutableStateOf(false) }
    val statusesStats = mangaDetails.statusesStats?.associate {
        simpleMapUserRateStatusToString(it.status, MediaType.MANGA) to it.count
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.Top)
    ) {
        mangaDetails.description?.let {
            ExpandableText(
                descriptionHtml = mangaDetails.descriptionHtml ?: "",
                modifier = Modifier.fillMaxWidth(),
                style = MaterialTheme.typography.bodySmall,
                linkColor = MaterialTheme.colorScheme.primary,
                brushColor = MaterialTheme.colorScheme.background.copy(0.8f),
                onEntityClick = { entityType, id ->
                    onEntityClick(entityType, id)
                }, onLinkClick = onLinkClick
            )
        }
        mangaDetails.personRoles?.let { personRoles ->
            AuthorSection(
                personRoles = personRoles,
                onPersonClick = { id ->
                    onEntityClick(EntityType.PERSON, id)
                },
                modifier = Modifier.fillMaxWidth()
            )
        }
        mangaDetails.characterRoles?.let { characterRoles ->
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = stringResource(R.string.details_characters),
                    style = MaterialTheme.typography.titleMedium
                )
                LazyRow(
                    modifier = Modifier.ignoreHorizontalParentPadding(horizontalPadding),
                    contentPadding = PaddingValues(horizontal = horizontalPadding),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(characterRoles) { characterItem ->
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
        statusesStats?.let {
            Box(
                modifier = Modifier.fillMaxWidth()
                    .padding(vertical = 12.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.surface)
            ) {
                SegmentedProgressBar(
                    groupedData = statusesStats.mapKeys { statusEntry ->
                        stringResource(id = statusEntry.key)
                    },
                    totalCount = mangaDetails.statusesStats.size,
                    modifier = Modifier.padding(12.dp),
                    itemShape = RoundedCornerShape(3.dp),
                    rowHeight = 16.dp,
                    rowShape = RoundedCornerShape(4.dp)
                )
            }
        }

        if(mangaDetails.related != null && mangaDetails.related.isNotEmpty()) {
            RelatedSection(
                relatedItems = mangaDetails.related.map { RelatedMapper.fromMangaRelated(it) },
                onItemClick = onItemClick,
                onArrowClick = { showRelatedBottomSheet = true }
            )
        }

        mangaDetails.topic?.id?.let { topicId ->
            CommentSection(
                topicId = topicId,
                isRefreshing = isRefreshing,
                onEntityClick = onEntityClick,
                onTopicNavigate = onTopicNavigate,
                onLinkClick = onLinkClick
            )
        }
    }

    if(showRelatedBottomSheet) {
        RelatedBottomSheet(
            relatedItems = mangaDetails.related?.map { RelatedMapper.fromMangaRelated(it) } ?: emptyList(),
            onItemClick = onItemClick,
            onDismiss = { showRelatedBottomSheet = false }
        )
    }
}

@Composable
private fun AuthorSection(
    personRoles: List<MangaDetailsQuery.PersonRole>,
    onPersonClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.Top)
    ) {
        Text(
            text = pluralStringResource(id = R.plurals.author_plural_form, count = personRoles.size),
            style = MaterialTheme.typography.titleMedium
        )
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
            maxItemsInEachRow = 2
        ) {
            personRoles.forEachIndexed { index, personRole ->
                AuthorItem(
                    personRole = personRole,
                    onPersonClick = onPersonClick,
                    modifier = if(index % 2 == 0 && index == personRoles.size - 1) {
                        Modifier.fillMaxWidth(0.5f)
                    } else Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun AuthorItem(
    personRole: MangaDetailsQuery.PersonRole,
    onPersonClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable { onPersonClick(personRole.person.id) },
        horizontalArrangement = Arrangement.spacedBy(6.dp, Alignment.Start)
    ) {
        BaseImage(
            model = personRole.person.poster?.originalUrl,
            modifier = Modifier.width(96.dp)
        )
        Column {
            Text(
                text = personRole.person.name,
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = personRole.rolesEn.first(),
                style = MaterialTheme.typography.bodySmall.copy(
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.75f)
                )
            )
        }
    }
}