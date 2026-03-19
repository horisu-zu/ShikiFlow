package com.example.shikiflow.presentation.screen.more.profile.stats.anilist

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.shikiflow.R
import com.example.shikiflow.domain.model.tracks.MediaType
import com.example.shikiflow.presentation.common.mappers.CountryOfOriginMapper.color
import com.example.shikiflow.presentation.common.mappers.CountryOfOriginMapper.displayValue
import com.example.shikiflow.presentation.common.mappers.MediaFormatMapper.color
import com.example.shikiflow.presentation.common.mappers.MediaFormatMapper.displayValue
import com.example.shikiflow.presentation.common.mappers.MediaTypeMapper.displayValue
import com.example.shikiflow.presentation.common.mappers.UserRateStatusMapper.color
import com.example.shikiflow.presentation.common.mappers.UserRateStatusMapper.mapStatus
import com.example.shikiflow.presentation.screen.more.profile.stats.StatsBarType
import com.example.shikiflow.presentation.viewmodel.user.statistics.UserStatsEvent
import com.example.shikiflow.presentation.viewmodel.user.statistics.UserStatsUiState

@Composable
fun OverviewSection(
    uiState: UserStatsUiState,
    event: UserStatsEvent,
    isCurrentUser: Boolean,
    horizontalPadding: Dp,
    onCompareClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val mediaTypes = remember(uiState.overviewStats) {
        uiState.overviewStats.getMediaTypes()
    }
    var currentMediaType by rememberSaveable { mutableStateOf(mediaTypes.first()) }

    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(
            start = horizontalPadding,
            end = horizontalPadding,
            bottom = 12.dp
        ),
        verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.Top)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    mediaTypes.forEach { mediaType ->
                        val isSelected = currentMediaType == mediaType

                        FilterChip(
                            selected = isSelected,
                            onClick = {
                                currentMediaType = mediaType
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
        }

        uiState.overviewStats[currentMediaType]?.let { overviewStats ->
            item {
                ShortStatsOverview(
                    mediaType = currentMediaType,
                    overviewStats = overviewStats,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            item {
                StatsVerticalChartComponent(
                    label = R.string.details_info_score_stats,
                    statsMap = mapOf(
                        StatsBarType.TITLES to overviewStats.scoreStatsTitles,
                        StatsBarType.TIME to overviewStats.scoreStatsTime
                    ),
                    mediaType = currentMediaType,
                    currentBarType = uiState.scoreBarType[currentMediaType] ?: StatsBarType.TITLES,
                    horizontalPadding = horizontalPadding,
                    onBarTypeChange = { scoreBarType ->
                        event.setScoreBarType(currentMediaType, scoreBarType)
                    }
                )
            }

            item {
                StatsVerticalChartComponent(
                    label = when (currentMediaType) {
                        MediaType.ANIME -> R.string.user_stats_length_episode_count
                        MediaType.MANGA -> R.string.user_stats_length_chapter_count
                    },
                    statsMap = mapOf(
                        StatsBarType.TITLES to overviewStats.lengthStatsTitles,
                        StatsBarType.TIME to overviewStats.lengthStatsTime,
                        StatsBarType.MEAN_SCORE to overviewStats.lengthStatsScore,
                    ),
                    mediaType = currentMediaType,
                    currentBarType = uiState.lengthBarType[currentMediaType] ?: StatsBarType.TITLES,
                    horizontalPadding = horizontalPadding,
                    onBarTypeChange = { lengthBarType ->
                        event.setLengthBarType(currentMediaType, lengthBarType)
                    }
                )
            }

            item {
                StatsHorizontalBarComponent(
                    label = R.string.details_info_statuses_stats,
                    stats = overviewStats.statusesStats,
                    statLabel = { status -> status.mapStatus(currentMediaType) },
                    statColor = { status -> status.color() },
                    horizontalPadding = horizontalPadding
                )
            }

            item {
                StatsHorizontalBarComponent(
                    label = R.string.user_stats_format,
                    stats = overviewStats.formatStats,
                    statLabel = { format -> format.displayValue() },
                    statColor = { format -> format.color() },
                    horizontalPadding = horizontalPadding
                )
            }

            item {
                StatsHorizontalBarComponent(
                    label = R.string.user_stats_country,
                    stats = overviewStats.countryStats,
                    statLabel = { country -> country.displayValue() },
                    statColor = { country -> country.color() },
                    horizontalPadding = horizontalPadding
                )
            }

            item {
                StatsVerticalChartComponent(
                    label = R.string.user_stats_release_year,
                    statsMap = mapOf(
                        StatsBarType.TITLES to overviewStats.releaseYearStatsTitles,
                        StatsBarType.TIME to overviewStats.releaseYearStatsTime,
                        StatsBarType.MEAN_SCORE to overviewStats.releaseYearStatsScore,
                    ),
                    mediaType = currentMediaType,
                    currentBarType = uiState.releaseYearBarType[currentMediaType] ?: StatsBarType.TITLES,
                    horizontalPadding = horizontalPadding,
                    onBarTypeChange = { releaseYearBarType ->
                        event.setReleaseYearBarType(currentMediaType, releaseYearBarType)
                    }
                )
            }

            if(overviewStats.startYearStatsTitles.isNotEmpty()) {
                item {
                    StatsVerticalChartComponent(
                        label = when(currentMediaType) {
                            MediaType.ANIME -> R.string.user_stats_watch_year
                            MediaType.MANGA -> R.string.user_stats_read_year
                        },
                        statsMap = mapOf(
                            StatsBarType.TITLES to overviewStats.startYearStatsTitles,
                            StatsBarType.TIME to overviewStats.startYearStatsTime,
                            StatsBarType.MEAN_SCORE to overviewStats.startYearStatsScore,
                        ),
                        mediaType = currentMediaType,
                        currentBarType = uiState.startYearBarType[currentMediaType] ?: StatsBarType.TITLES,
                        horizontalPadding = horizontalPadding,
                        onBarTypeChange = { startYearBarType ->
                            event.setStartYearBarType(currentMediaType, startYearBarType)
                        }
                    )
                }
            }
        }
    }
}