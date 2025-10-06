package com.example.shikiflow.presentation.screen.main.details.anime.watch.player

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.shikiflow.R
import com.example.shikiflow.domain.model.kodik.KodikLink

@Composable
fun PlayerTopComponent(
    title: String,
    episodeNum: Int,
    episodesCount: Int,
    currentQuality: String,
    translationGroup: String,
    qualityData: KodikLink?,
    onNavigateBack: () -> Unit,
    onQualityChange: (String) -> Unit,
    onEpisodeChange: (Int) -> Unit,
    onExpand: (Boolean) -> Unit,
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
            val qualities = qualityData.qualityLink.entries.toList()

            if(episodesCount > 1) {
                PlayerDropdown(
                    label = stringResource(R.string.media_item_episode, episodeNum),
                    values = (1..episodesCount).map { epNum ->
                        stringResource(R.string.media_item_episode, epNum)
                    },
                    onValueChange = { index ->
                        onEpisodeChange(index + 1)
                    },
                    onExpand = onExpand
                )
            }
            PlayerDropdown(
                label = "${currentQuality}P",
                values = qualities.map { "${it.key}P" },
                onValueChange = { index ->
                    onQualityChange(qualities[index].key)
                },
                onExpand = onExpand
            )
        }
    }
}

@Composable
private fun PlayerDropdown(
    label: String,
    values: List<String>,
    onValueChange: (Int) -> Unit,
    onExpand: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    var dropdownExpanded by remember { mutableStateOf(false) }
    var itemHeight by remember { mutableIntStateOf(0) }
    val scrollState = rememberScrollState()

    LaunchedEffect(dropdownExpanded) {
        if(dropdownExpanded) {
            scrollState.scrollTo(value = itemHeight * values.indexOf(label))
        }
    }

    Box(
        modifier = modifier
    ) {
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .clickable {
                    dropdownExpanded = true
                    onExpand(true)
                }.padding(horizontal = 8.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(2.dp, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium.copy(
                    color = Color.White
                )
            )
            Icon(
                imageVector = Icons.Default.KeyboardArrowDown,
                contentDescription = "Expand Dropdown",
                tint = Color.White
            )
        }
        DropdownMenu(
            scrollState = scrollState,
            expanded = dropdownExpanded,
            onDismissRequest = {
                dropdownExpanded = false
                onExpand(false)
            },
            modifier = Modifier.heightIn(max = 200.dp),
            containerColor = Color.Black.copy(alpha = 0.75f),
        ) {
            values.forEachIndexed { index, value ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = value,
                            style = MaterialTheme.typography.labelMedium.copy(
                                color = Color.White
                            )
                        )
                    },
                    modifier = Modifier
                        .background(
                            color = if(value != label) {
                                Color.Transparent
                            } else { Color.White.copy(alpha = 0.25f) }
                        )
                        .onSizeChanged { intSize ->
                            itemHeight = intSize.height
                        }
                    ,
                    onClick = {
                        if(value != label) onValueChange(index)
                        dropdownExpanded = false
                        onExpand(false)
                    }
                )
            }
        }
    }
}