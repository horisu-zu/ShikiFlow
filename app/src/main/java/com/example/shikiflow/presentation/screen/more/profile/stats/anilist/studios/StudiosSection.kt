package com.example.shikiflow.presentation.screen.more.profile.stats.anilist.studios

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.shikiflow.R
import com.example.shikiflow.domain.model.media_details.StudioShort
import com.example.shikiflow.domain.model.tracks.MediaType
import com.example.shikiflow.domain.model.user.stats.StudioStat
import com.example.shikiflow.presentation.common.ErrorItem
import com.example.shikiflow.presentation.common.mappers.ProfileMapper.sortedBy
import com.example.shikiflow.presentation.screen.more.profile.stats.StatsBarType
import com.example.shikiflow.presentation.screen.more.profile.stats.anilist.TypeSelector
import com.example.shikiflow.presentation.screen.more.profile.stats.anilist.tags.StatItem

@Composable
fun StudiosSection(
    typeStats: List<StudioStat>,
    statsBarType: StatsBarType,
    isLoading: Boolean,
    horizontalPadding: Dp,
    onBarTypeChange: (StatsBarType) -> Unit,
    onStudioClick: (StudioShort) -> Unit,
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
        if(typeStats.isNotEmpty()) {
            item(span = { GridItemSpan(maxCurrentLineSpan) }) {
                TypeSelector(
                    types = StatsBarType.entries,
                    mediaType = MediaType.ANIME,
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
                mediaType = MediaType.ANIME
            ).forEachIndexed { index, studioStat ->
                item(key = studioStat.studioShort.id) {
                    StatItem(
                        stat = studioStat,
                        positionNumber = index + 1,
                        mediaType = MediaType.ANIME,
                        onClick = { onStudioClick(studioStat.studioShort) },
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