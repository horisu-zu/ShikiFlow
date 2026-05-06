package com.example.shikiflow.presentation.screen.more.profile

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
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
import com.example.shikiflow.domain.model.user.User
import com.example.shikiflow.domain.model.user.stats.OverviewStats
import com.example.shikiflow.domain.model.user.stats.MediaTypeStats
import com.example.shikiflow.domain.model.user.stats.OverviewStatType
import com.example.shikiflow.presentation.common.mappers.MediaTypeMapper.displayValue
import com.example.shikiflow.presentation.common.mappers.MediaTypeMapper.iconResource
import com.example.shikiflow.presentation.screen.more.profile.stats.anilist.AnilistUserStatsSection
import com.example.shikiflow.presentation.screen.more.profile.stats.shikimori.ShikimoriTrackItem
import com.example.shikiflow.presentation.viewmodel.user.statistics.UserStatsViewModel

@Composable
fun UserStatsSection(
    userData: User,
    typesList: List<MediaType>,
    isCurrentUser: Boolean,
    horizontalPadding: Dp,
    navOptions: ProfileNavOptions,
    userStatsViewModel: UserStatsViewModel = hiltViewModel()
) {
    val uiState by userStatsViewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        userStatsViewModel.setInitialParams(userData.id, typesList)
    }

    uiState.authType?.let { authType ->
        when(authType) {
            AuthType.SHIKIMORI -> {
                ShikimoriTrackSection(
                    userRateData = uiState.overviewStats,
                    typesList = uiState.typesList,
                    currentType = uiState.mediaType,
                    isCurrentUser = isCurrentUser,
                    onTypeSelect = { mediaType ->
                        userStatsViewModel.setMediaType(mediaType)
                    },
                    onCompareClick = {
                        navOptions.navigateToCompare(userData)
                    },
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = horizontalPadding)
                )
            }
            AuthType.ANILIST -> {
                AnilistUserStatsSection(
                    uiState = uiState,
                    isCurrentUser = isCurrentUser,
                    onCompareClick = {
                        navOptions.navigateToCompare(userData)
                    },
                    horizontalPadding = horizontalPadding,
                    event = userStatsViewModel,
                    navOptions = navOptions,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}

@Composable
fun ShikimoriTrackSection(
    userRateData: MediaTypeStats<OverviewStats>,
    typesList: List<MediaType>,
    currentType: MediaType,
    isCurrentUser: Boolean,
    onTypeSelect: (MediaType) -> Unit,
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
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                typesList.forEach { mediaType ->
                    val isSelected = currentType == mediaType

                    FilterChip(
                        selected = isSelected,
                        onClick = { onTypeSelect(mediaType) },
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

        userRateData[currentType]?.let { mediaStats ->
            ShikimoriTrackItem(
                mediaType = currentType,
                iconResource = currentType.iconResource(),
                type = stringResource(id = currentType.displayValue()),
                mediaStats = mediaStats,
                itemsCount = mediaStats.shortStats.find { it.statType == OverviewStatType.TITLE }
                    ?.count?.toInt() ?: 0,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}