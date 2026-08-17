package com.example.shikiflow.presentation.screen.main.details.anime.watch.player

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.requiredSizeIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.DropdownMenuGroup
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.DropdownMenuPopup
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEachIndexed
import com.example.shikiflow.R
import com.example.shikiflow.presentation.common.ChipWithMenu

@Composable
fun PlayerTopComponent(
    title: String,
    episodeNum: Int,
    episodesList: List<Int>,
    currentQuality: String,
    translationGroup: String,
    qualityData: List<String>?,
    onNavigateBack: () -> Unit,
    onQualityChange: (String) -> Unit,
    onEpisodeChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.Start)
    ) {
        IconButton(onClick = onNavigateBack) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Navigate Back",
                modifier = Modifier.size(24.dp),
                tint = Color.White
            )
        }

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp, Alignment.Top)
        ) {
            Text(
                text = title,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
            )

            Text(
                text = translationGroup,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = Color.White.copy(alpha = 0.65f)
                )
            )
        }

        qualityData?.let {
            if(episodesList.size > 1) {
                EpisodeDropdown(
                    values = episodesList,
                    selectedValue = episodeNum,
                    onValueChange = { episodeNum ->
                        onEpisodeChange(episodeNum)
                    },
                    itemLabel = { episodeNum ->
                        stringResource(R.string.media_item_episode, episodeNum)
                    }
                )
            }

            ChipWithMenu(
                title = {
                    Text(
                        text = "${currentQuality}P"
                    )
                },
                values = qualityData,
                selectedValue = currentQuality,
                onValueSelected = { quality ->
                    onQualityChange(quality)
                },
                itemLabel = { quality ->
                    "${quality}P"
                }
            )
        }
    }
}

@Composable
private fun EpisodeDropdown(
    values: List<Int>,
    selectedValue: Int,
    onValueChange: (Int) -> Unit,
    itemLabel: @Composable (Int) -> String,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    var itemHeight by remember { mutableIntStateOf(0) }
    val windowHeight = with(LocalDensity.current) {
        LocalWindowInfo.current.containerSize.height.toDp()
    }
    val scrollState = rememberScrollState()

    LaunchedEffect(expanded, itemHeight) {
        if (expanded && itemHeight != 0) {
            scrollState.scrollTo(value = itemHeight * values.indexOf(selectedValue))
        }
    }

    Box(
        modifier = modifier.wrapContentSize(Alignment.TopStart)
    ) {
        FilterChip(
            selected = true,
            onClick = { expanded = true },
            label = {
                Text(
                    text = itemLabel(selectedValue)
                )
            },
            modifier = Modifier.heightIn(max = 32.dp)
        )

        DropdownMenuPopup(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.requiredSizeIn(maxHeight = windowHeight / 2)
        ) {
            DropdownMenuGroup(
                shapes = MenuDefaults.groupShapes(),
                modifier = Modifier.verticalScroll(scrollState)
            ) {
                values.fastForEachIndexed { index, item ->
                    DropdownMenuItem(
                        checked = selectedValue == item,
                        onCheckedChange = {
                            onValueChange(item)
                            expanded = false
                        },
                        text = {
                            Text(
                                text = itemLabel(item)
                            )
                        },
                        shapes = MenuDefaults.itemShape(index, values.size),
                        colors = MenuDefaults.selectableItemColors(
                            selectedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                            selectedTextColor = MaterialTheme.colorScheme.onSecondaryContainer,
                            selectedLeadingIconColor = MaterialTheme.colorScheme.onSecondaryContainer
                        ),
                        checkedLeadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Done,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                        },
                        modifier = Modifier.onSizeChanged { size ->
                            if (itemHeight == 0) itemHeight = size.height
                        }
                    )
                }
            }
        }
    }
}