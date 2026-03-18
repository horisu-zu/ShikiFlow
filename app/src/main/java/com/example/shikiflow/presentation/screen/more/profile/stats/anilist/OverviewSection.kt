package com.example.shikiflow.presentation.screen.more.profile.stats.anilist

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.shikiflow.R
import com.example.shikiflow.presentation.common.mappers.MediaTypeMapper.displayValue
import com.example.shikiflow.presentation.screen.more.profile.stats.StatsBarType
import com.example.shikiflow.presentation.viewmodel.user.statistics.UserStatsEvent
import com.example.shikiflow.presentation.viewmodel.user.statistics.UserStatsUiState

@Composable
fun OverviewSection(
    uiState: UserStatsUiState,
    event: UserStatsEvent,
    isCurrentUser: Boolean,
    onCompareClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val mediaTypes = remember(uiState.overviewStats) {
        uiState.overviewStats.getMediaTypes()
    }
    var selectedMediaType by rememberSaveable { mutableStateOf(mediaTypes.first()) }

    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(bottom = 12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.Top)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                mediaTypes.forEach { mediaType ->
                    val isSelected = selectedMediaType == mediaType

                    FilterChip(
                        selected = isSelected,
                        onClick = {
                            selectedMediaType = mediaType
                        },
                        label = {
                            Text(
                                text = stringResource(id = mediaType.displayValue())
                            )
                        },
                        leadingIcon = if(isSelected) {
                            {
                                Icon(
                                    imageVector = Icons.Default.Done,
                                    contentDescription = null
                                )
                            }
                        } else { null }
                    )
                }
            }
            if(!isCurrentUser) {
                Row(
                    modifier = Modifier
                        .clip(CircleShape)
                        .clickable { onCompareClick() }
                        .padding(start = 8.dp, top = 4.dp, bottom = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(2.dp, Alignment.Start),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.more_profile_compare),
                        style = typography.bodyMedium
                    )
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }

        uiState.overviewStats[selectedMediaType]?.let { overviewStats ->
            AnilistMediaTracksItem(
                overviewStats = overviewStats,
                scoreBarType = uiState.scoreBarType[selectedMediaType] ?: StatsBarType.TITLES,
                lengthBarType = uiState.lengthBarType[selectedMediaType] ?: StatsBarType.TITLES,
                mediaType = selectedMediaType,
                onScoreBarTypeChange = { scoreBarType ->
                    event.setScoreBarType(selectedMediaType, scoreBarType)
                },
                onLengthBarTypeChange = { lengthBarType ->
                    event.setLengthBarType(selectedMediaType, lengthBarType)
                },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}