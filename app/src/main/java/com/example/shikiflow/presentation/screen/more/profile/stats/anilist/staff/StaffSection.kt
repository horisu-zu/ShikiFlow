package com.example.shikiflow.presentation.screen.more.profile.stats.anilist.staff

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.shikiflow.R
import com.example.shikiflow.domain.model.tracks.MediaType
import com.example.shikiflow.domain.model.user.stats.StaffStat
import com.example.shikiflow.presentation.common.ErrorItem
import com.example.shikiflow.presentation.common.image.BaseImage
import com.example.shikiflow.presentation.common.image.ImageType
import com.example.shikiflow.presentation.common.mappers.MediaTypeMapper.displayValue
import com.example.shikiflow.presentation.common.mappers.ProfileMapper.formatDaysHours
import com.example.shikiflow.presentation.common.mappers.ProfileMapper.sortedBy
import com.example.shikiflow.presentation.screen.more.profile.stats.StatsBarType
import com.example.shikiflow.presentation.screen.more.profile.stats.anilist.StatType
import com.example.shikiflow.presentation.screen.more.profile.stats.anilist.StatType.Companion.displayValue
import com.example.shikiflow.presentation.screen.more.profile.stats.anilist.TypeSelector

@Composable
fun StaffSection(
    staffStats: List<StaffStat>?,
    staffBarType: StatsBarType,
    typesList: List<MediaType>,
    currentMediaType: MediaType,
    isLoading: Boolean,
    horizontalPadding: Dp,
    onMediaTypeChange: (MediaType) -> Unit,
    onStaffBarTypeChange: (StatsBarType) -> Unit,
    onStaffClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(300.dp),
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

        staffStats?.let { stats ->
            if(stats.isNotEmpty()) {
                item(span = { GridItemSpan(maxCurrentLineSpan) }) {
                    TypeSelector(
                        types = StatsBarType.entries,
                        mediaType = currentMediaType,
                        currentType = staffBarType,
                        onTypeSelect = { staffBarType ->
                            onStaffBarTypeChange(staffBarType)
                        },
                        modifier = Modifier.wrapContentWidth(Alignment.Start)
                    )
                }

                stats.sortedBy(
                    type = staffBarType,
                    mediaType = currentMediaType
                ).forEachIndexed { index, staffStat ->
                    item(key = staffStat.staffShort.id) {
                        StaffStatItem(
                            staffStat = staffStat,
                            positionNumber = index + 1,
                            mediaType = currentMediaType,
                            onStaffClick = { staffId ->
                                onStaffClick(staffId)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .animateItem()
                        )
                    }
                }
            } else if(!isLoading) {
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
fun StaffStatItem(
    staffStat: StaffStat,
    positionNumber: Int,
    mediaType: MediaType,
    onStaffClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val resources = LocalResources.current
    val stats = remember(staffStat, mediaType) {
        mapOf(
            StatType.COUNT to staffStat.count,
            StatType.MEAN_SCORE to staffStat.meanScore,
            StatType.TIME to when (mediaType) {
                MediaType.ANIME -> staffStat.timeWatched
                MediaType.MANGA -> staffStat.chaptersRead
            }
        )
    }

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .padding(all = 12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = staffStat.staffShort.fullName,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier
                    .offset(x = (-6).dp)
                    .clip(CircleShape)
                    .clickable { onStaffClick(staffStat.staffShort.id) }
                    .padding(horizontal = 6.dp, vertical = 4.dp)
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
            horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.Start)
        ) {
            val imageType = ImageType.Poster()

            BaseImage(
                model = staffStat.staffShort.imageUrl,
                imageType = imageType,
                onClick = { onStaffClick(staffStat.staffShort.id) }
            )

            Column(
                modifier = Modifier
                    .height(
                        height = (imageType.width.value / imageType.aspectRatio).dp
                    ),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                stats.forEach { (type, value) ->
                    val statValue = if(mediaType == MediaType.ANIME && type == StatType.TIME) {
                        resources.formatDaysHours(value.toFloat())
                    } else value.toString()

                    Column {
                        Text(
                            text = statValue,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.SemiBold
                            )
                        )

                        Text(
                            text = stringResource(id = type.displayValue(mediaType)),
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = MaterialTheme.colorScheme.onSurface.copy(
                                    alpha = 0.75f
                                )
                            )
                        )
                    }
                }
            }
        }
    }
}