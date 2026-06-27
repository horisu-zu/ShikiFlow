package com.example.shikiflow.presentation.screen.more.profile.stats.anilist

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.shikiflow.R
import com.example.shikiflow.domain.model.tracks.MediaType
import com.example.shikiflow.presentation.common.ErrorItem
import com.example.shikiflow.presentation.common.SnapFlingLazyRow
import com.example.shikiflow.presentation.common.mappers.ProfileMapper.displayValue
import com.example.shikiflow.presentation.screen.main.details.DetailsNavRoute
import com.example.shikiflow.presentation.screen.more.profile.ProfileNavOptions
import com.example.shikiflow.presentation.screen.more.profile.stats.StatsBarType
import com.example.shikiflow.presentation.screen.more.profile.stats.UserStatsSectionType
import com.example.shikiflow.presentation.screen.more.profile.stats.anilist.overview.OverviewSection
import com.example.shikiflow.presentation.screen.more.profile.stats.anilist.staff.StaffSection
import com.example.shikiflow.presentation.screen.more.profile.stats.anilist.studios.StudiosSection
import com.example.shikiflow.presentation.screen.more.profile.stats.anilist.tags.TypeStatSection
import com.example.shikiflow.presentation.viewmodel.user.statistics.UserStatsEvent
import com.example.shikiflow.presentation.viewmodel.user.statistics.UserStatsUiState

@Composable
fun AnilistUserStatsSection(
    uiState: UserStatsUiState,
    isCurrentUser: Boolean,
    horizontalPadding: Dp,
    event: UserStatsEvent,
    navOptions: ProfileNavOptions,
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
                StatsSectionItem(
                    stats = (uiState.overviewStats.animeStats ?: uiState.overviewStats.mangaStats)
                        ?.shortStats.takeIf { !it.isNullOrEmpty() },
                    errorMessage = uiState.errorMessage,
                    onRetryClick = { event.onRefresh(uiState.statsSectionType) }
                ) {
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
                StatsSectionItem(
                    stats = uiState.genreStats.stats?.get(uiState.mediaType),
                    errorMessage = uiState.genreStats.errorMessage,
                    onRetryClick = { event.onRefresh(uiState.statsSectionType) }
                ) {
                    TypeStatSection(
                        isLoading = uiState.genreStats.isLoading,
                        typeStats = uiState.genreStats.stats?.get(uiState.mediaType) ?: emptyList(),
                        statsBarType = uiState.genresBarType[uiState.mediaType] ?: StatsBarType.TITLES,
                        typesList =  uiState.typesList,
                        currentMediaType = uiState.mediaType,
                        onMediaTypeChange = { mediaType ->
                            event.setMediaType(mediaType)
                        },
                        onBarTypeChange = { statsBarType ->
                            event.setGenresBarType(statsBarType)
                        },
                        horizontalPadding = horizontalPadding,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
            UserStatsSectionType.TAGS -> {
                StatsSectionItem(
                    stats = uiState.tagsStats.stats?.get(uiState.mediaType),
                    errorMessage = uiState.tagsStats.errorMessage,
                    onRetryClick = { event.onRefresh(uiState.statsSectionType) }
                ) {
                    TypeStatSection(
                        isLoading = uiState.tagsStats.isLoading,
                        typeStats = uiState.tagsStats.stats?.get(uiState.mediaType) ?: emptyList(),
                        statsBarType = uiState.tagsBarType[uiState.mediaType] ?: StatsBarType.TITLES,
                        typesList =  uiState.typesList,
                        currentMediaType = uiState.mediaType,
                        onMediaTypeChange = { mediaType ->
                            event.setMediaType(mediaType)
                        },
                        onBarTypeChange = { statsBarType ->
                            event.setTagsBarType(statsBarType)
                        },
                        horizontalPadding = horizontalPadding,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
            UserStatsSectionType.STAFF -> {
                StatsSectionItem(
                    stats = uiState.staffStats.stats?.get(uiState.mediaType),
                    errorMessage = uiState.staffStats.errorMessage,
                    onRetryClick = { event.onRefresh(uiState.statsSectionType) }
                ) {
                    StaffSection(
                        isLoading = uiState.staffStats.isLoading,
                        staffStats = uiState.staffStats.stats?.get(uiState.mediaType) ?: emptyList(),
                        staffBarType = uiState.staffBarType[uiState.mediaType] ?: StatsBarType.TITLES,
                        typesList = uiState.typesList,
                        currentMediaType = uiState.mediaType,
                        horizontalPadding = horizontalPadding,
                        onMediaTypeChange = { mediaType ->
                            event.setMediaType(mediaType)
                        },
                        onStaffBarTypeChange = { staffBarType ->
                            event.setStaffBarType(staffBarType)
                        },
                        onStaffClick = { staffId ->
                            navOptions.navigateToDetails(DetailsNavRoute.Staff(staffId))
                        },
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
            UserStatsSectionType.VOICE_ACTORS -> {
                StatsSectionItem(
                    stats = uiState.voiceActorsStats.stats,
                    errorMessage = uiState.voiceActorsStats.errorMessage,
                    onRetryClick = { event.onRefresh(uiState.statsSectionType) }
                ) {
                    StaffSection(
                        isLoading = uiState.voiceActorsStats.isLoading,
                        staffStats = uiState.voiceActorsStats.stats ?: emptyList(),
                        staffBarType = uiState.voiceActorsBarType,
                        typesList = listOf(MediaType.ANIME),
                        currentMediaType = MediaType.ANIME,
                        horizontalPadding = horizontalPadding,
                        onMediaTypeChange = { mediaType ->
                            event.setMediaType(mediaType)
                        },
                        onStaffBarTypeChange = { voiceActorsBarType ->
                            event.setVoiceActorsBarType(voiceActorsBarType)
                        },
                        onStaffClick = { staffId ->
                            navOptions.navigateToDetails(DetailsNavRoute.Staff(staffId))
                        },
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
            UserStatsSectionType.STUDIOS -> {
                StatsSectionItem(
                    stats = uiState.studiosStats.stats,
                    errorMessage = uiState.studiosStats.errorMessage,
                    onRetryClick = { event.onRefresh(uiState.statsSectionType) }
                ) {
                    StudiosSection(
                        isLoading = uiState.studiosStats.isLoading,
                        typeStats = uiState.studiosStats.stats ?: emptyList(),
                        statsBarType = uiState.studiosBarType,
                        horizontalPadding = horizontalPadding,
                        onBarTypeChange = { studiosBarType ->
                            event.setStudiosBarType(studiosBarType)
                        },
                        onStudioClick = { studio ->
                            navOptions.navigateToDetails(DetailsNavRoute.Studio(studio.id, studio.name))
                        },
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}

@Composable
private fun <T> StatsSectionItem(
    stats: List<T>?,
    errorMessage: String?,
    onRetryClick: () -> Unit,
    content: @Composable () -> Unit
) {
    if(errorMessage != null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            ErrorItem(
                message = errorMessage,
                buttonLabel = stringResource(R.string.common_retry),
                onButtonClick = { onRetryClick() }
            )
        }
    } else if(stats?.isEmpty() == true) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            ErrorItem(
                message = stringResource(R.string.stats_empty_label)
            )
        }
    } else {
        content()
    }
}