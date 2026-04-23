package com.example.shikiflow.presentation.screen.main

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.input.clearText
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AppBarWithSearch
import androidx.compose.material3.ExpandedFullScreenSearchBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.SearchBarScrollBehavior
import androidx.compose.material3.SearchBarValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberSearchBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.shikiflow.R
import com.example.shikiflow.domain.model.tracks.MediaType
import com.example.shikiflow.presentation.common.mappers.MediaTypeMapper.iconResource
import com.example.shikiflow.presentation.screen.MainNavOptions
import com.example.shikiflow.presentation.screen.main.details.DetailsNavRoute
import com.example.shikiflow.presentation.viewmodel.anime.tracks.search.TracksSearchViewModel
import com.example.shikiflow.utils.toIcon
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, FlowPreview::class)
@Composable
fun TracksSearchBar(
    currentTrackMode: MediaType,
    scrollBehavior: SearchBarScrollBehavior,
    onModeChange: (MediaType) -> Unit,
    mainNavOptions: MainNavOptions,
    tracksSearchViewModel: TracksSearchViewModel = hiltViewModel()
) {
    val searchBarState = rememberSearchBarState()
    val textFieldState = rememberTextFieldState()
    val scope = rememberCoroutineScope()

    val isSearchActive = remember(searchBarState.currentValue) {
        searchBarState.currentValue == SearchBarValue.Expanded
    }

    LaunchedEffect(textFieldState) {
        snapshotFlow { textFieldState.text.toString() }
            .debounce(300)
            .collect { tracksSearchViewModel.onQueryChange(it) }
    }

    BackHandler(enabled = isSearchActive) {
        scope.launch { searchBarState.animateToCollapsed() }
    }

    val animatedHorizontalPadding by animateDpAsState(
        targetValue = if (isSearchActive) 8.dp
            else 0.dp
    )

    val selectorBackgroundColor = when(isSearchActive) {
        true -> MaterialTheme.colorScheme.background
        false -> MaterialTheme.colorScheme.surfaceContainer
    }

    val inputField = @Composable {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(end = animatedHorizontalPadding),
            horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.Start),
            verticalAlignment = Alignment.CenterVertically
        ) {
            when(isSearchActive) {
                true -> {
                    SearchBarDefaults.InputField(
                        searchBarState = searchBarState,
                        textFieldState = textFieldState,
                        onSearch = { /**/ },
                        placeholder = {
                            Text(
                                text = stringResource(R.string.tracks_page_search)
                            )
                        },
                        shape = RoundedCornerShape(16.dp),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainer
                        ),
                        leadingIcon = {
                            when(searchBarState.currentValue) {
                                SearchBarValue.Collapsed -> {
                                    IconButton(
                                        onClick = {
                                            scope.launch {
                                                searchBarState.animateToExpanded()
                                            }
                                        }
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Search,
                                            contentDescription = "Search"
                                        )
                                    }
                                }
                                SearchBarValue.Expanded -> {
                                    IconButton(
                                        onClick = {
                                            scope.launch {
                                                searchBarState.animateToCollapsed()
                                            }
                                        }
                                    ) {
                                        Icon(
                                            imageVector = Icons.AutoMirrored.Default.KeyboardArrowLeft,
                                            contentDescription = "Exit Search"
                                        )
                                    }
                                }
                            }
                        },
                        trailingIcon = {
                            if(textFieldState.text.isNotBlank()) {
                                IconButton(
                                    onClick = { textFieldState.clearText() }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Clear,
                                        contentDescription = "Clear Query"
                                    )
                                }
                            }
                        },
                        modifier = Modifier.weight(1f)
                    )
                }
                false -> {
                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(16.dp))
                            .background(MaterialTheme.colorScheme.surfaceContainer)
                            .clickable {
                                scope.launch {
                                    searchBarState.animateToExpanded()
                                }
                            },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = {
                                scope.launch {
                                    searchBarState.animateToExpanded()
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Search"
                            )
                        }

                        Text(
                            text = stringResource(R.string.tracks_page_search)
                        )
                    }
                }
            }

            TracksTypeSelector(
                currentType = currentTrackMode,
                onModeChange = onModeChange,
                backgroundColor = selectorBackgroundColor,
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(selectorBackgroundColor)
                    .padding(all =  8.dp)
            )
        }
    }

    Column {
        AppBarWithSearch(
            state = searchBarState,
            inputField = inputField,
            scrollBehavior = scrollBehavior,
            windowInsets = WindowInsets.statusBars,
            shape = RectangleShape,
            colors = SearchBarDefaults.appBarWithSearchColors(
                searchBarColors = SearchBarDefaults.colors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        )

        if(isSearchActive) {
            HorizontalDivider()
        }

        ExpandedFullScreenSearchBar(
            state = searchBarState,
            inputField = inputField
        ) {
            TracksSearchBarComponent(
                mediaType = currentTrackMode,
                isAppBarVisible = true,
                onMediaClick = { mediaType, id ->
                    val detailsNavRoute = when(mediaType) {
                        MediaType.ANIME -> DetailsNavRoute.AnimeDetails(id)
                        MediaType.MANGA -> DetailsNavRoute.MangaDetails(id)
                    }

                    mainNavOptions.navigateToDetails(detailsNavRoute)
                }
            )
        }
    }
}

@Composable
private fun TracksTypeSelector(
    currentType: MediaType,
    onModeChange: (MediaType) -> Unit,
    backgroundColor: Color,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        MediaType.entries.forEach { mediaType ->
            val isCurrent = currentType == mediaType

            mediaType.iconResource().toIcon(
                tint = when(isCurrent) {
                    true -> MaterialTheme.colorScheme.onPrimary
                    false -> MaterialTheme.colorScheme.onBackground
                },
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { onModeChange(mediaType) }
                    .background(
                        color = when(isCurrent) {
                            true -> MaterialTheme.colorScheme.primary
                            false -> backgroundColor
                        }
                    )
                    .padding(all = 6.dp)
            )
        }
    }
}