package com.example.shikiflow.presentation.screen.main.details.common

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.shikiflow.presentation.common.image.BaseImage
import com.example.shikiflow.presentation.common.image.ImageType

@Composable
fun CharacterCard(
    characterPoster: String?,
    characterName: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val shapePercent = 16

    Column(
        modifier = modifier
            .clip(
                shape = RoundedCornerShape(
                    topStartPercent = shapePercent,
                    topEndPercent = shapePercent,
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
            imageType = ImageType.Custom(
                width = Int.MAX_VALUE.dp,
                aspectRatio = 2f / 2.85f,
                clip = RoundedCornerShape(shapePercent)
            )
        )
        Text(
            text = characterName,
            overflow = TextOverflow.Ellipsis,
            maxLines = 1,
            style = MaterialTheme.typography.labelSmall
        )
    }
}