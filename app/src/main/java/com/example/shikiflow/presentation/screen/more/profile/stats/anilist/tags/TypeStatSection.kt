package com.example.shikiflow.presentation.screen.more.profile.stats.anilist.tags

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.shikiflow.R
import com.example.shikiflow.domain.model.tracks.MediaType
import com.example.shikiflow.domain.model.user.stats.CombinedStat
import com.example.shikiflow.domain.model.user.stats.StudioStat
import com.example.shikiflow.domain.model.user.stats.TypeStat
import com.example.shikiflow.presentation.common.ErrorItem
import com.example.shikiflow.presentation.common.mappers.MediaTypeMapper.displayValue
import com.example.shikiflow.presentation.common.mappers.ProfileMapper.formatDaysHours
import com.example.shikiflow.presentation.common.mappers.ProfileMapper.sortedBy
import com.example.shikiflow.presentation.screen.more.profile.stats.StatsBarType
import com.example.shikiflow.presentation.screen.more.profile.stats.anilist.ShortStatsOverviewItem
import com.example.shikiflow.presentation.screen.more.profile.stats.anilist.StatType
import com.example.shikiflow.presentation.screen.more.profile.stats.anilist.StatType.Companion.displayValue
import com.example.shikiflow.presentation.screen.more.profile.stats.anilist.StatType.Companion.iconResource
import com.example.shikiflow.presentation.screen.more.profile.stats.anilist.TypeSelector
import kotlin.collections.component1
import kotlin.collections.component2

@Composable
fun TypeStatSection(
    typeStats: List<TypeStat>?,
    statsBarType: StatsBarType,
    typesList: List<MediaType>,
    currentMediaType: MediaType,
    onMediaTypeChange: (MediaType) -> Unit,
    onBarTypeChange: (StatsBarType) -> Unit,
    horizontalPadding: Dp,
    modifier: Modifier = Modifier
) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(240.dp),
        modifier = modifier,
        contentPadding = PaddingValues(
            start = horizontalPadding,
            end = horizontalPadding,
            bottom = 12.dp
        ),
        verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.Top),
        horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.Start)
    ) {
        if(typesList.size > 1) {
            item(span = { GridItemSpan(maxCurrentLineSpan) }) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    typesList.forEach { mediaType ->
                        val isSelected = currentMediaType == mediaType

                        FilterChip(
                            selected = isSelected,
                            onClick = { onMediaTypeChange(mediaType) },
                            label = {
                                Text(
                                    text = stringResource(id = mediaType.displayValue())
                                )
                            },
                            leadingIcon = if (isSelected) {
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
            }
        }

        typeStats?.let { typeStats ->
            if(typeStats.isNotEmpty()) {
                item(span = { GridItemSpan(maxCurrentLineSpan) }) {
                    TypeSelector(
                        types = StatsBarType.entries,
                        mediaType = currentMediaType,
                        currentType = statsBarType,
                        onTypeSelect = { typeBarType ->
                            onBarTypeChange(typeBarType)
                        },
                        horizontalPadding = horizontalPadding,
                        modifier = Modifier.wrapContentWidth(Alignment.Start)
                    )
                }

                typeStats.sortedBy(
                    type = statsBarType,
                    mediaType = currentMediaType
                ).forEachIndexed { index, typeStat ->
                    item(key = typeStat.type) {
                        StatItem(
                            stat = typeStat,
                            positionNumber = index + 1,
                            mediaType = currentMediaType,
                            modifier = Modifier
                                .fillMaxWidth()
                                .animateItem()
                        )
                    }
                }
            } else {
                item(span = { GridItemSpan(maxCurrentLineSpan) }) {
                    ErrorItem(
                        message = stringResource(R.string.stats_empty_label)
                    )
                }
            }
        }
    }
}

@Composable
fun StatItem(
    stat: CombinedStat,
    positionNumber: Int,
    mediaType: MediaType,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    val resources = LocalResources.current
    val stats = remember(stat, mediaType) {
        mapOf(
            StatType.COUNT to stat.count,
            StatType.MEAN_SCORE to stat.meanScore,
            StatType.TIME to when (mediaType) {
                MediaType.ANIME -> stat.timeWatched
                MediaType.MANGA -> stat.chaptersRead
            }
        )
    }

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .then(
                other = onClick?.let {
                    Modifier.clickable { onClick() }
                } ?: Modifier
            )
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val label = when(stat) {
                is TypeStat -> stat.type
                is StudioStat -> stat.studioShort.name
                else -> ""
            }

            Text(
                text = label,
                style = MaterialTheme.typography.titleLarge
            )

            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = positionNumber.toString(),
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            stats.forEach { (type, value) ->
                val statValue = if(mediaType == MediaType.ANIME && type == StatType.TIME) {
                    resources.formatDaysHours(value.toFloat())
                } else value.toString()

                ShortStatsOverviewItem(
                    label = stringResource(id = type.displayValue(mediaType)),
                    value = statValue,
                    iconResource = type.iconResource(mediaType),
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}