package com.example.shikiflow.presentation.screen.browse

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.shikiflow.R
import com.example.shikiflow.domain.model.browse.BrowseMedia
import com.example.shikiflow.domain.model.tracks.MediaType
import com.example.shikiflow.presentation.common.mappers.MediaFormatMapper.displayValue

@Composable
fun BrowseGridItem(
    browseItem: BrowseMedia,
    onItemClick: (Int, MediaType) -> Unit,
    modifier: Modifier = Modifier
) {
    val mutableInteractionSource = remember { MutableInteractionSource() }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = mutableInteractionSource,
                indication = null,
                onClick = { onItemClick(browseItem.id, browseItem.mediaType) }
            )
    ) {
        BrowseCoverItem(
            posterUrl = browseItem.posterUrl,
            mediaType = browseItem.mediaType,
            userRateStatus = browseItem.userRateStatus,
            coverWidth = Int.MAX_VALUE.dp,
            cornerShape = 12.dp
        )

        Text(
            text = browseItem.title,
            style = MaterialTheme.typography.labelSmall,
            overflow = TextOverflow.Ellipsis,
            maxLines = 2,
            modifier = Modifier.padding(top = 4.dp)
        )

        Text(
            text = listOfNotNull(
                browseItem.mediaFormat?.displayValue()?.let { formatRes ->
                    stringResource(id = formatRes)
                },
                browseItem.score?.let { score ->
                    stringResource(id = R.string.media_score, score)
                }
            ).joinToString(" • "),
            style = MaterialTheme.typography.labelSmall
        )
    }
}