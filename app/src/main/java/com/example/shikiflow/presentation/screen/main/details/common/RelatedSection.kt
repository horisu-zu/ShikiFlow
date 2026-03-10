package com.example.shikiflow.presentation.screen.main.details.common

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.shikiflow.R
import com.example.shikiflow.domain.model.media_details.RelatedMedia
import com.example.shikiflow.domain.model.tracks.MediaType
import com.example.shikiflow.presentation.common.image.RoundedImage
import com.example.shikiflow.presentation.common.mappers.MediaFormatMapper.displayValue
import com.example.shikiflow.presentation.common.mappers.RelationKindMapper.displayValue

@Composable
fun RelatedSection(
    relatedItems: List<RelatedMedia>,
    onItemClick: (Int, MediaType) -> Unit,
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
                        color = MaterialTheme.colorScheme.onPrimary
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
                        imageVector = Icons.Default.KeyboardArrowDown,
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
                    relatedMedia = relatedItem,
                    onItemClick = onItemClick
                )
            }
        }
    }
}

@Composable
fun RelatedItem(
    relatedMedia: RelatedMedia,
    onItemClick: (Int, MediaType) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .clickable {
                onItemClick(relatedMedia.id, relatedMedia.mediaType)
            },
        horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.Start),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RoundedImage(
            model = relatedMedia.coverImageUrl,
            clip = RoundedCornerShape(8.dp),
            contentScale = ContentScale.Crop,
            modifier = Modifier.size(48.dp)
        )
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = relatedMedia.title,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.labelMedium
            )
            relatedMedia.let { media ->
                Text(
                    text = buildString {
                        append(stringResource(id = media.mediaFormat.displayValue()))
                        append(" ∙ ")
                        append(stringResource(id = media.relationKind.displayValue()))
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