package com.example.shikiflow.presentation.screen.main.details.common

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.example.shikiflow.R
import com.example.shikiflow.domain.model.media_details.MediaTag
import com.example.shikiflow.presentation.common.mappers.TagMapper.displayValue

@Composable
fun MediaTagItem(
    mediaTag: MediaTag,
    modifier: Modifier = Modifier,
    style: TextStyle = MaterialTheme.typography.labelMedium
) {
    var showSpoiler by remember { mutableStateOf(!mediaTag.isSpoiler) }
    val itemShape = RoundedCornerShape(percent = 16)

    val textColor by animateColorAsState(
        targetValue = if(showSpoiler) MaterialTheme.colorScheme.onSecondaryContainer
            else MaterialTheme.colorScheme.onErrorContainer,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessMediumLow
        )
    )

    val backgroundColor by animateColorAsState(
        targetValue = if(showSpoiler) MaterialTheme.colorScheme.secondaryContainer
            else MaterialTheme.colorScheme.errorContainer,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessMediumLow
        )
    )

    Box(
        modifier = modifier
            .clip(itemShape)
            .then(
                if(mediaTag.isSpoiler) {
                    Modifier
                        .clickable { showSpoiler = !showSpoiler }
                        .border(
                            width = 1.dp,
                            color = textColor,
                            shape = itemShape
                        )
                } else Modifier
            )
            .background(backgroundColor)
            .animateContentSize()
    ) {
        Text(
            text = stringResource(
                id = if(showSpoiler) {
                    mediaTag.tag.displayValue()
                } else R.string.spoiler_label
            ),
            style = style.copy(
                color = textColor
            ),
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
        )
    }
}