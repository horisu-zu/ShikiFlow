package com.example.shikiflow.presentation.screen.more.profile.stats.anilist

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.shikiflow.domain.model.tracks.MediaType
import com.example.shikiflow.presentation.common.mappers.ProfileMapper.displayValue
import com.example.shikiflow.presentation.screen.more.profile.stats.StatsBarType
import kotlin.collections.forEach

@Composable
fun TypeSelector(
    types: List<StatsBarType>,
    mediaType: MediaType,
    currentType: StatsBarType,
    onTypeSelect: (StatsBarType) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(all = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.Start),
        verticalAlignment = Alignment.CenterVertically
    ) {
        types.forEach { statsBarType ->
            val isSelected = statsBarType == currentType
            val backgroundAlpha by animateFloatAsState(
                targetValue = if(isSelected) 1f else 0f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioNoBouncy,
                    stiffness = Spring.StiffnessLow
                )
            )
            val textColor by animateColorAsState(
                targetValue = if(isSelected) MaterialTheme.colorScheme.onPrimary
                    else MaterialTheme.colorScheme.onSurface,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioNoBouncy,
                    stiffness = Spring.StiffnessLow
                )
            )

            Text(
                text = stringResource(statsBarType.displayValue(mediaType)),
                style = MaterialTheme.typography.bodySmall.copy(
                    textAlign = TextAlign.Center,
                    color = textColor
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .clip(CircleShape)
                    .clickable {
                        if (!isSelected) {
                            onTypeSelect(statsBarType)
                        }
                    }
                    .background(
                        color = MaterialTheme.colorScheme.primary.copy(
                            alpha = backgroundAlpha
                        )
                    )
                    .padding(
                        horizontal = 6.dp,
                        vertical = 4.dp
                    )
            )
        }
    }
}