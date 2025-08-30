package com.example.shikiflow.presentation.screen.main.details.manga

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.example.graphql.MangaDetailsQuery
import com.example.shikiflow.domain.model.mapper.RelatedMapper
import com.example.shikiflow.domain.model.mapper.UserRateMapper.Companion.mapStatusToString
import com.example.shikiflow.domain.model.tracks.MediaType
import com.example.shikiflow.presentation.common.ExpandableText
import com.example.shikiflow.presentation.common.SegmentedProgressBar
import com.example.shikiflow.presentation.screen.main.details.RelatedBottomSheet
import com.example.shikiflow.presentation.screen.main.details.anime.CharacterCard
import com.example.shikiflow.presentation.screen.main.details.anime.RelatedSection
import com.example.shikiflow.utils.Converter.EntityType

@Composable
fun MangaDetailsDesc(
    mangaDetails: MangaDetailsQuery.Manga?,
    onItemClick: (String, MediaType) -> Unit,
    onEntityClick: (EntityType, String) -> Unit,
    onLinkClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var showRelatedBottomSheet by remember { mutableStateOf(false) }

    ConstraintLayout(
        modifier = modifier.fillMaxWidth()
    ) {
        val (descRef, charactersRef, listRef ,relatedRef) = createRefs()

        ExpandableText(
            descriptionHtml = mangaDetails?.descriptionHtml ?: "No Description",
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

        Column(
            modifier = Modifier.constrainAs(charactersRef) {
                top.linkTo(descRef.bottom, margin = 8.dp)
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
                items(mangaDetails?.characterRoles ?: emptyList()) { characterItem ->
                    CharacterCard(
                        characterPoster = characterItem.character.characterShort.poster
                            ?.posterShort?.previewUrl,
                        characterName = characterItem.character.characterShort.name,
                        onClick = { onEntityClick(EntityType.CHARACTER, characterItem.character.characterShort.id) }
                    )
                }
            }
        }

        SegmentedProgressBar(
            groupedData = mangaDetails?.statusesStats?.associate {
                mapStatusToString(it.status, MediaType.MANGA) to it.count
            } ?: emptyMap(),
            totalCount = mangaDetails?.statusesStats?.size ?: 0,
            modifier = Modifier.constrainAs(listRef) {
                top.linkTo(charactersRef.bottom, margin = 12.dp)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                width = Dimension.fillToConstraints
            }
        )

        if(mangaDetails?.related != null && mangaDetails.related.isNotEmpty()) {
            RelatedSection(
                relatedItems = mangaDetails.related.map { RelatedMapper.fromMangaRelated(it) },
                onItemClick = onItemClick,
                onArrowClick = { showRelatedBottomSheet = true },
                modifier = Modifier.constrainAs(relatedRef) {
                    top.linkTo(listRef.bottom, margin = 12.dp)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    width = Dimension.fillToConstraints
                }
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