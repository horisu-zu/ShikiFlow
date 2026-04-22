package com.example.shikiflow.presentation.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.shikiflow.domain.model.track.UserRateStatus
import com.example.shikiflow.domain.model.tracks.MediaType
import com.example.shikiflow.presentation.common.image.BaseImage
import com.example.shikiflow.presentation.common.image.ImageType
import com.example.shikiflow.presentation.common.mappers.ColorMapper.color
import com.example.shikiflow.presentation.common.mappers.UserRateIconProvider.icon
import com.example.shikiflow.utils.toIcon
import com.materialkolor.ktx.harmonize

@Composable
fun BrowseCoverItem(
    posterUrl: String?,
    mediaType: MediaType,
    userRateStatus: UserRateStatus?,
    coverWidth: Dp,
    cornerShape: Dp,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    isOnTop: Boolean = false
) {
    val statusShape = when(isOnTop) {
        true -> RoundedCornerShape(
            topStart = 0.dp,
            bottomStart = cornerShape,
            topEnd = cornerShape,
            bottomEnd = 0.dp
        )
        false -> RoundedCornerShape(
            bottomEnd = cornerShape,
            bottomStart = 0.dp,
            topEnd = 0.dp,
            topStart = cornerShape
        )
    }

    Box(
        modifier = modifier
    ) {
        BaseImage(
            model = posterUrl,
            contentScale = ContentScale.Crop,
            imageType = ImageType.Poster(
                width = coverWidth,
                clip = RoundedCornerShape(cornerShape)
            ),
            onClick = onClick
        )
        userRateStatus ?.let { status ->
            Box(
                modifier = Modifier
                    .align(
                        alignment = when(isOnTop) {
                            true -> Alignment.TopEnd
                            false -> Alignment.BottomEnd
                        }
                    )
                    .clip(shape = statusShape)
                    .background(
                        color = status
                            .color()
                            .harmonize(MaterialTheme.colorScheme.background)
                    ),
                contentAlignment = Alignment.Center
            ) {
                userRateStatus.icon(mediaType).toIcon(
                    modifier = Modifier
                        .padding(
                            horizontal = 8.dp,
                            vertical = 6.dp
                        )
                        .size(20.dp),
                    tint = Color.White
                )
            }
        }
    }
}