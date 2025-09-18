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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.shikiflow.domain.model.anime.Browse
import com.example.shikiflow.domain.model.tracks.MediaType
import com.example.shikiflow.presentation.common.image.BaseImage
import com.example.shikiflow.presentation.common.image.ImageType

@Composable
fun BrowseGridItem(
    browseItem: Browse,
    onItemClick: (String, MediaType) -> Unit,
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
        BaseImage(
            model = browseItem.posterUrl,
            contentScale = ContentScale.Crop,
            imageType = ImageType.Poster(
                defaultWidth = Int.MAX_VALUE.dp,
            )
        )

        Text(
            text = browseItem.title,
            style = MaterialTheme.typography.labelSmall,
            overflow = TextOverflow.Ellipsis,
            maxLines = 2,
            modifier = Modifier.padding(top = 4.dp)
        )

        Text(
            text = "${browseItem.kind} • ${browseItem.score}★",
            style = MaterialTheme.typography.labelSmall.copy(
                fontSize = 10.sp
            )
        )
    }
}