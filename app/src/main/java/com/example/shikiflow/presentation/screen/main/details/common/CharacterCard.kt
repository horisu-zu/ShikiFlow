package com.example.shikiflow.presentation.screen.main.details.common

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import com.example.shikiflow.presentation.common.image.BaseImage
import com.example.shikiflow.presentation.common.image.ImageType
import com.example.shikiflow.presentation.common.shimmerEffect

@Composable
fun CharacterCard(
    characterPoster: String?,
    characterName: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    clipPercent: Int = 16,
    imageType: ImageType = ImageType.Custom(
        width = Int.MAX_VALUE.dp,
        aspectRatio = 2f / 2.85f,
        shape = RoundedCornerShape(clipPercent)
    )
) {
    Column(
        modifier = modifier
            .width(imageType.width)
            .clip(
                shape = RoundedCornerShape(
                    topStartPercent = clipPercent,
                    topEndPercent = clipPercent,
                    bottomStartPercent = 4,
                    bottomEndPercent = 4
                )
            )
            .clickable { onClick() },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        BaseImage(
            model = characterPoster,
            imageType = imageType
        )

        Text(
            text = characterName,
            overflow = TextOverflow.Ellipsis,
            maxLines = 1,
            style = MaterialTheme.typography.labelSmall
        )
    }
}

@Composable
fun CharacterCardPlaceholder(
    itemIndex: Int,
    imageType: ImageType,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .width(imageType.width)
            .shimmerEffect(overContent = true),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(imageType.aspectRatio)
                .clip(imageType.shape)
                .background(MaterialTheme.colorScheme.onSurface)
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            repeat(2) { index ->
                val indexValue = itemIndex % 3 + 1

                Box(
                    modifier = Modifier
                        .width(24.dp + indexValue * 12.dp - index * 6.dp)
                        .height(MaterialTheme.typography.labelSmall.lineHeight.value.dp)
                        .clip(RoundedCornerShape(percent = 32))
                        .background(MaterialTheme.colorScheme.onSurface)
                )

                Box(
                    modifier = Modifier
                        .width(60.dp - indexValue * 8.dp + index * 4.dp)
                        .height(MaterialTheme.typography.labelSmall.lineHeight.value.dp)
                        .clip(RoundedCornerShape(percent = 32))
                        .background(MaterialTheme.colorScheme.onSurface)
                )
            }
        }
    }
}