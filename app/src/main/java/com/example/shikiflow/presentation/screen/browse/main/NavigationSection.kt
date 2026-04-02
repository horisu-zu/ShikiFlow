package com.example.shikiflow.presentation.screen.browse.main

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.shikiflow.domain.model.browse.BrowseType
import com.example.shikiflow.domain.model.tracks.MediaType
import com.example.shikiflow.presentation.common.mappers.BrowseTypeMapper.displayValue
import com.example.shikiflow.presentation.common.mappers.BrowseTypeMapper.iconResource
import com.example.shikiflow.presentation.common.mappers.MediaTypeMapper.displayValue
import com.example.shikiflow.utils.IconResource
import com.example.shikiflow.utils.toIcon

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun NavigationSection(
    modifier: Modifier = Modifier,
    onNavigateSideScreen: (BrowseType) -> Unit
) {
    var currentType by rememberSaveable { mutableStateOf(MediaType.ANIME) }

    Column(
        modifier = modifier
    ) {
        Row(
            modifier = modifier
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                .padding(all = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp, Alignment.Start),
            verticalAlignment = Alignment.CenterVertically
        ) {
            MediaType.entries.forEach { mediaType ->
                val isSelected = mediaType == currentType
                val backgroundAlpha by animateFloatAsState(
                    targetValue = if(isSelected) 1f else 0f,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioNoBouncy,
                        stiffness = Spring.StiffnessMediumLow
                    )
                )
                val textColor by animateColorAsState(
                    targetValue = if(isSelected) MaterialTheme.colorScheme.onPrimary
                        else MaterialTheme.colorScheme.onSurface,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioNoBouncy,
                        stiffness = Spring.StiffnessMediumLow
                    )
                )

                Text(
                    text = stringResource(id = mediaType.displayValue()),
                    style = MaterialTheme.typography.titleMedium.copy(
                        textAlign = TextAlign.Center,
                        color = textColor
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .clickable {
                            if (!isSelected) {
                                currentType = mediaType
                            }
                        }
                        .background(
                            color = MaterialTheme.colorScheme.primary.copy(
                                alpha = backgroundAlpha
                            )
                        )
                        .padding(
                            horizontal = 8.dp,
                            vertical = 6.dp
                        )
                )
            }
        }
        AnimatedContent(
            targetState = currentType,
            transitionSpec = {
                expandVertically(
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioNoBouncy,
                        stiffness = Spring.StiffnessMediumLow
                    )
                ) togetherWith fadeOut(
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioNoBouncy,
                        stiffness = Spring.StiffnessMediumLow
                    )
                ) + shrinkVertically(
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioNoBouncy,
                        stiffness = Spring.StiffnessMediumLow
                    )
                )
            },
            modifier = Modifier.padding(top = 12.dp)
        ) { mediaType ->
            FlowRow(
                maxItemsInEachRow = 2,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                when(mediaType) {
                    MediaType.ANIME -> BrowseType.AnimeBrowseType.entries
                    MediaType.MANGA -> BrowseType.MangaBrowseType.entries
                }.forEach { browseType ->
                    NavigationCard(
                        icon = browseType.iconResource(),
                        title = stringResource(browseType.displayValue()),
                        onClick = {
                            onNavigateSideScreen(browseType)
                        },
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxRowHeight()
                            .padding(
                                horizontal = 12.dp,
                                vertical = 4.dp
                            )
                    )
                }
            }
        }
    }
}

@Composable
private fun NavigationCard(
    icon: IconResource,
    title: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceContainerHigh)
            .clickable { onClick() }
            .then(modifier),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        icon.toIcon(
            modifier = Modifier.size(24.dp),
            tint = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = title,
            style = MaterialTheme.typography.labelLarge.copy(
                color = MaterialTheme.colorScheme.onSurface
            )
        )
    }
}