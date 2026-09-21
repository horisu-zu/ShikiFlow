package com.example.shikiflow.presentation.screen.main.details.anime.video

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.shikiflow.R
import com.example.shikiflow.domain.model.media_details.VideoShort
import com.example.shikiflow.presentation.common.SnapFlingLazyRow
import com.example.shikiflow.presentation.common.TextWithDivider
import com.example.shikiflow.presentation.common.ignoreHorizontalParentPadding
import com.example.shikiflow.presentation.common.image.BaseImage
import com.example.shikiflow.presentation.common.image.ImageType

@Composable
fun VideoSection(
    videos: List<VideoShort>,
    onVideoClick: (String) -> Unit,
    horizontalPadding: Dp,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        TextWithDivider(
            text = stringResource(R.string.anime_details_video)
        )

        SnapFlingLazyRow(
            modifier = Modifier
                .ignoreHorizontalParentPadding(horizontalPadding)
                .fillMaxWidth()
                .animateContentSize(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(horizontal = horizontalPadding)
        ) {
            items(videos) { video ->
                VideoItem(
                    title = video.title,
                    thumbnailUrl = video.thumbnailUrl,
                    onVideoClick = { onVideoClick(video.url) }
                )
            }
        }
    }
}

@Composable
private fun VideoItem(
    title: String?,
    thumbnailUrl: String,
    onVideoClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val imageType = ImageType.Screenshot(shape = RoundedCornerShape(16.dp))

    Column(
        modifier = modifier
            .clip(imageType.shape)
            .clickable { onVideoClick() }
    ) {
        BaseImage(
            model = thumbnailUrl,
            imageType = imageType
        )

        title?.let {
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .width(imageType.width)
                    .padding(horizontal = 6.dp, vertical = 4.dp)
            )
        }
    }
}