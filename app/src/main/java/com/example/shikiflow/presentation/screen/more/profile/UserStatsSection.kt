package com.example.shikiflow.presentation.screen.more.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.shikiflow.R
import com.example.shikiflow.domain.model.auth.AuthType
import com.example.shikiflow.domain.model.tracks.MediaType
import com.example.shikiflow.domain.model.user.OverviewStats
import com.example.shikiflow.domain.model.user.MediaTypeStats
import com.example.shikiflow.domain.model.user.OverviewStatType
import com.example.shikiflow.presentation.common.PullToRefreshCustomBox
import com.example.shikiflow.presentation.common.SnapFlingLazyRow
import com.example.shikiflow.presentation.common.mappers.MediaTypeMapper.displayValue
import com.example.shikiflow.presentation.common.mappers.MediaTypeMapper.iconResource
import com.example.shikiflow.presentation.common.mappers.ProfileMapper.displayValue
import com.example.shikiflow.presentation.screen.more.profile.stats.anilist.OverviewSection
import com.example.shikiflow.presentation.screen.more.profile.stats.UserStatsSectionType
import com.example.shikiflow.presentation.screen.more.profile.stats.shikimori.ShikimoriTrackItem
import com.example.shikiflow.presentation.viewmodel.user.statistics.UserStatsEvent
import com.example.shikiflow.presentation.viewmodel.user.statistics.UserStatsUiState
import com.example.shikiflow.presentation.viewmodel.user.statistics.UserStatsViewModel
import com.example.shikiflow.utils.ignoreHorizontalParentPadding

@Composable
fun UserStatsSection(
    userId: Int,
    isCurrentUser: Boolean,
    isRefreshEnabled: Boolean,
    horizontalPadding: Dp,
    onCompareClick: () -> Unit,
    userStatsViewModel: UserStatsViewModel = hiltViewModel()
) {
    val uiState by userStatsViewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        userStatsViewModel.setUserId(userId)
    }

    PullToRefreshCustomBox(
        isRefreshing = uiState.isLoading,
        enabled = isRefreshEnabled,
        onRefresh = { userStatsViewModel.onRefresh() },
        modifier = Modifier.fillMaxSize()
    ) {
        uiState.authType?.let { authType ->
            when(authType) {
                AuthType.SHIKIMORI -> {
                    ShikimoriTrackSection(
                        userRateData = uiState.overviewStats,
                        isCurrentUser = isCurrentUser,
                        onCompareClick = onCompareClick,
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(
                                horizontal = horizontalPadding,
                                vertical = 12.dp
                            )
                    )
                }
                AuthType.ANILIST -> {
                    AnilistStatsSection(
                        uiState = uiState,
                        isCurrentUser = isCurrentUser,
                        onCompareClick = onCompareClick,
                        event = userStatsViewModel,
                        horizontalPadding = horizontalPadding,
                    )
                }
            }
        }
    }
}

@Composable
fun ShikimoriTrackSection(
    userRateData: MediaTypeStats<OverviewStats>,
    isCurrentUser: Boolean,
    onCompareClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.Top)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(id = R.string.profile_screen_track_lists_label),
                style = typography.titleLarge
            )

            if(!isCurrentUser) {
                Row(
                    modifier = Modifier.clip(CircleShape)
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

        MediaType.entries.forEach { mediaType ->
            userRateData[mediaType]?.let { mediaStats ->
                ShikimoriTrackItem(
                    mediaType = mediaType,
                    iconResource = mediaType.iconResource(),
                    type = stringResource(id = mediaType.displayValue()),
                    mediaStats = mediaStats,
                    itemsCount = mediaStats.shortStats.find { it.statType == OverviewStatType.TITLE }
                        ?.count?.toInt() ?: 0,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.surfaceContainerLow)
                        .padding(all = 12.dp)
                )
            }
        }
    }
}

@Composable
private fun AnilistStatsSection(
    uiState: UserStatsUiState,
    isCurrentUser: Boolean,
    horizontalPadding: Dp,
    event: UserStatsEvent,
    onCompareClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.Top)
    ) {
        SnapFlingLazyRow(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = horizontalPadding),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(UserStatsSectionType.entries) { statsSectionType ->
                val isSelected = uiState.statsSectionType == statsSectionType

                FilterChip(
                    selected = isSelected,
                    onClick = {
                        event.setStatsSectionType(statsSectionType)
                    },
                    label = {
                        Text(
                            text = stringResource(id = statsSectionType.displayValue())
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
        when(uiState.statsSectionType) {
            UserStatsSectionType.OVERVIEW -> {
                if(!uiState.overviewStats.isEmpty()) {
                    OverviewSection(
                        uiState = uiState,
                        isCurrentUser = isCurrentUser,
                        event = event,
                        horizontalPadding = horizontalPadding,
                        onCompareClick = onCompareClick
                    )
                }
            }
            UserStatsSectionType.GENRES -> {
                /**/
            }
            UserStatsSectionType.TAGS -> {
                /**/
            }
            UserStatsSectionType.STAFF -> {
                /**/
            }
            UserStatsSectionType.SEYU -> {
                /**/
            }
            UserStatsSectionType.STUDIOS -> {
                /**/
            }
        }
    }
}