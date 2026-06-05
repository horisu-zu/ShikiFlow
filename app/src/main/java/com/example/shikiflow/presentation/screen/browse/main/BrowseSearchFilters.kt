package com.example.shikiflow.presentation.screen.browse.main

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.shikiflow.R
import com.example.shikiflow.domain.model.auth.AuthType
import com.example.shikiflow.domain.model.media_details.AgeRating
import com.example.shikiflow.domain.model.media_details.MediaSeason
import com.example.shikiflow.domain.model.media_details.MediaSeasonEnum
import com.example.shikiflow.domain.model.media_details.MediaStatus
import com.example.shikiflow.domain.model.search.MediaBrowseOptions
import com.example.shikiflow.domain.model.sort.MediaSort
import com.example.shikiflow.domain.model.sort.Sort
import com.example.shikiflow.domain.model.sort.SortDirection
import com.example.shikiflow.domain.model.track.MediaFormat
import com.example.shikiflow.domain.model.tracks.MediaType
import com.example.shikiflow.presentation.common.ChipWithMenu
import com.example.shikiflow.presentation.common.SnapFlingLazyRow
import com.example.shikiflow.presentation.common.ignoreHorizontalParentPadding
import com.example.shikiflow.presentation.common.mappers.AgeRatingMapper.displayValue
import com.example.shikiflow.presentation.common.mappers.GenreMapper.displayValue
import com.example.shikiflow.presentation.common.mappers.MediaFormatMapper.displayValue
import com.example.shikiflow.presentation.common.mappers.MediaStatusMapper.displayValue
import com.example.shikiflow.presentation.common.mappers.MediaTypeMapper.displayValue
import com.example.shikiflow.presentation.common.mappers.SeasonMapper.displayValue
import com.example.shikiflow.presentation.common.mappers.SeasonMapper.iconResource
import com.example.shikiflow.presentation.common.mappers.SortMapper.displayValue
import com.example.shikiflow.presentation.common.mappers.TagMapper.displayValue
import com.example.shikiflow.utils.DateUtils
import com.example.shikiflow.utils.toIcon

@Composable
fun BrowseSearchFilters(
    horizontalPadding: Dp,
    authType: AuthType,
    searchOptions: MediaBrowseOptions,
    onOptionsChanged: (MediaBrowseOptions) -> Unit
) {
    val showGenreBottomSheet = remember { mutableStateOf(false) }

    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.Top)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            MediaType.entries.forEach { item ->
                FilterChip(
                    selected = searchOptions.mediaType == item,
                    onClick = {
                        onOptionsChanged(
                            searchOptions.copy(
                                mediaType = item
                            )
                        )
                    },
                    label = { Text(stringResource(item.displayValue())) },
                    leadingIcon = if(searchOptions.mediaType == item) {
                         {
                            Icon(
                                imageVector = Icons.Default.Done,
                                contentDescription = null
                            )
                        }
                    } else { null },
                    modifier = Modifier.height(32.dp)
                )
            }
        }

        SnapFlingLazyRow(
            modifier = Modifier
                .ignoreHorizontalParentPadding(horizontalPadding)
                .fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = horizontalPadding),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            item {
                ChipWithMenu(
                    title = stringResource(R.string.common_default),
                    values = listOf(
                        MediaSort.Common.entries,
                        when(authType) {
                            AuthType.SHIKIMORI -> MediaSort.Shikimori.entries
                            AuthType.ANILIST -> MediaSort.Anilist.entries
                        }
                    ).flatten(),
                    selectedValue = searchOptions.sort?.type,
                    onValueSelected = { sortType ->
                        onOptionsChanged(
                            searchOptions.copy(
                                sort = if(sortType == searchOptions.sort?.type) null
                                    else Sort(
                                        type = sortType,
                                        direction = searchOptions.sort?.direction ?: SortDirection.DESCENDING
                                    )
                            )
                        )
                    },
                    itemLabel = { stringResource(it.displayValue()) },
                    leadingIcon = {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_sort_descending),
                            contentDescription = null,
                            tint = if(searchOptions.sort?.type != null) LocalContentColor.current
                                else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                )
            }

            if(authType == AuthType.ANILIST) {
                item {
                    AnimatedVisibility(
                        visible = searchOptions.sort?.type != null,
                        enter = fadeIn() + expandHorizontally(),
                        exit = fadeOut() + shrinkHorizontally()
                    ) {
                        ChipWithMenu(
                            title = stringResource(R.string.browse_search_label_sort_by),
                            values = SortDirection.entries,
                            selectedValue = searchOptions.sort?.direction,
                            onValueSelected = { direction ->
                                onOptionsChanged(
                                    searchOptions.copy(
                                        sort = searchOptions.sort?.copy(
                                            direction = direction
                                        )
                                    )
                                )
                            },
                            itemLabel = { stringResource(it.displayValue()) }
                        )
                    }
                }
            }
        }

        SnapFlingLazyRow(
            modifier = Modifier
                .ignoreHorizontalParentPadding(horizontalPadding)
                .fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = horizontalPadding),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            item {
                ChipWithMenu(
                    title = stringResource(R.string.browse_search_label_format),
                    values = MediaFormat.entries.filter { formatEntry ->
                        formatEntry.mediaType == searchOptions.mediaType
                    }.filter { formatEntry ->
                        authType in formatEntry.supportedBy
                    },
                    selectedValue = searchOptions.format,
                    onValueSelected = { format ->
                        onOptionsChanged(
                            searchOptions.copy(
                                format = if(searchOptions.format == format) null
                                    else format
                            )
                        )
                    },
                    itemLabel = { stringResource(it.displayValue()) }
                )
            }
            item {
                ChipWithMenu(
                    title = stringResource(R.string.browse_search_label_status),
                    values = MediaStatus.entries.filter { statusEntry ->
                        authType to searchOptions.mediaType !in statusEntry.exclusions
                    }.filter { status ->
                        searchOptions.mediaType in status.mediaType
                    },
                    selectedValue = searchOptions.status,
                    onValueSelected = { status ->
                        onOptionsChanged(
                            searchOptions.copy(
                                status = if(searchOptions.status == status) null
                                    else status
                            )
                        )
                    },
                    itemLabel = { stringResource(it.displayValue()) }
                )
            }
            item {
                AnimatedVisibility(
                    visible = authType == AuthType.SHIKIMORI && searchOptions.mediaType == MediaType.ANIME,
                    enter = fadeIn() + expandHorizontally(),
                    exit = fadeOut() + shrinkHorizontally()
                ) {
                    ChipWithMenu(
                        title = stringResource(R.string.browse_search_label_age_rating),
                        values = AgeRating.entries,
                        selectedValue = searchOptions.ageRating,
                        onValueSelected = { ageRating ->
                            onOptionsChanged(
                                searchOptions.copy(
                                    ageRating = if(searchOptions.ageRating == ageRating) null
                                        else ageRating
                                )
                            )
                        },
                        itemLabel = { stringResource(it.displayValue()) }
                    )
                }
            }
        }

        AnimatedVisibility(
            visible = searchOptions.mediaType == MediaType.ANIME,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            SnapFlingLazyRow(
                modifier = Modifier
                    .ignoreHorizontalParentPadding(horizontalPadding)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = horizontalPadding),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                item {
                    ChipWithMenu(
                        title = stringResource(R.string.browse_search_label_season),
                        values = MediaSeasonEnum.entries,
                        selectedValue = searchOptions.season?.seasonEnum,
                        onValueSelected = { season ->
                            onOptionsChanged(
                                searchOptions.copy(
                                    season = MediaSeason(
                                        seasonEnum = if(searchOptions.season?.seasonEnum == season) null
                                            else season,
                                        year = searchOptions.season?.year
                                    )
                                )
                            )
                        },
                        itemLabel = { stringResource(it.displayValue()) },
                        itemLeadingIcon = { season ->
                            season.iconResource()
                                .toIcon(
                                    modifier = Modifier.size(20.dp)
                                )
                        },
                        leadingIcon = if(searchOptions.season?.seasonEnum != null) {
                            {
                                searchOptions.season.seasonEnum
                                    .iconResource()
                                    .toIcon(
                                        modifier = Modifier.size(20.dp)
                                    )
                            }
                        } else { null }
                    )
                }
                item {
                    ChipWithMenu(
                        title = stringResource(R.string.browse_search_label_season_year),
                        values = DateUtils.seasonYears(),
                        selectedValue = searchOptions.season?.year,
                        onValueSelected = { year ->
                            onOptionsChanged(
                                searchOptions.copy(
                                    season = MediaSeason(
                                        seasonEnum = searchOptions.season?.seasonEnum,
                                        year = if(searchOptions.season?.year == year) null
                                            else year
                                    )
                                )
                            )
                        },
                        itemLabel = { it.toString() }
                    )
                }
            }
        }

        SnapFlingLazyRow(
            modifier = Modifier
                .ignoreHorizontalParentPadding(horizontalPadding)
                .fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = horizontalPadding),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if(searchOptions.genres.isNotEmpty()) {
                items(searchOptions.genres.size) { index ->
                    val selectedGenre = searchOptions.genres[index]

                    FilterChip(
                        selected = true,
                        onClick = {
                            onOptionsChanged(
                                searchOptions.copy(
                                    genres = searchOptions.genres - selectedGenre
                                )
                            )
                        },
                        label = {
                            Text(
                                stringResource(selectedGenre.displayValue())
                            )
                        },
                        modifier = Modifier.height(32.dp)
                    )
                }
            }
            if(searchOptions.tags.isNotEmpty()) {
                items(searchOptions.tags.size) { index ->
                    val selectedTag = searchOptions.tags[index]

                    FilterChip(
                        selected = true,
                        onClick = {
                            onOptionsChanged(
                                searchOptions.copy(
                                    tags = searchOptions.tags - selectedTag
                                )
                            )
                        },
                        label = {
                            Text(
                                stringResource(selectedTag.displayValue())
                            )
                        },
                        modifier = Modifier.height(32.dp)
                    )
                }
            }
            item {
                FilterChip(
                    selected = false,
                    onClick = { showGenreBottomSheet.value = true },
                    label = { Text(stringResource(R.string.browse_search_filters_add_genre)) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Add,
                            tint = MaterialTheme.colorScheme.onSurface,
                            contentDescription = null
                        )
                    },
                    modifier = Modifier.height(32.dp)
                )
            }
        }

        if(showGenreBottomSheet.value) {
            GenreBottomSheet(
                authType = authType,
                searchOptions = searchOptions,
                onOptionsChanged = onOptionsChanged,
                onDismiss = { showGenreBottomSheet.value = false }
            )
        }
    }
}