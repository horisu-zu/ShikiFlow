package com.example.shikiflow.presentation.screen.browse

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.shikiflow.R
import com.example.shikiflow.domain.model.browse.BrowseMedia
import com.example.shikiflow.domain.model.tracks.MediaType
import com.example.shikiflow.presentation.common.BrowseCoverItem
import com.example.shikiflow.presentation.common.image.ImageType
import com.example.shikiflow.presentation.common.mappers.MediaFormatMapper.displayValue
import com.example.shikiflow.utils.foregroundGradient

@Composable
fun BrowseCardItem(
    browseItem: BrowseMedia,
    onItemClick: (Int, MediaType) -> Unit,
    modifier: Modifier = Modifier
) {
    val clip = 12.dp
    val imageType = ImageType.Poster(
        width = Int.MAX_VALUE.dp,
        clip = RoundedCornerShape(clip)
    )

    Box(
        modifier = modifier
            .clip(imageType.clip)
            .clickable { onItemClick(browseItem.id, browseItem.mediaType) }
    ) {
        BrowseCoverItem(
            posterUrl = browseItem.posterUrl,
            mediaType = browseItem.mediaType,
            userRateStatus = browseItem.userRateStatus,
            coverWidth = imageType.width,
            cornerShape = clip,
            isOnTop = true,
            modifier = Modifier.foregroundGradient(
                gradientColors = listOf(
                    Color.Transparent,
                    MaterialTheme.colorScheme.background
                ),
                startY = 0.6f
            )
        )

        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(horizontal = 6.dp, vertical = 4.dp)
        ) {
            Text(
                text = browseItem.title,
                style = MaterialTheme.typography.labelMedium,
                overflow = TextOverflow.Ellipsis,
                maxLines = 2
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
}