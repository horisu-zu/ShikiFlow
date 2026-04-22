package com.example.shikiflow.presentation.screen.browse.main.anilist

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
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
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.shikiflow.R
import com.example.shikiflow.domain.model.browse.BrowseType
import com.example.shikiflow.domain.model.tracks.MediaType
import com.example.shikiflow.presentation.common.ErrorItem
import com.example.shikiflow.presentation.common.PullToRefreshCustomBox
import com.example.shikiflow.presentation.common.SnapFlingLazyRow
import com.example.shikiflow.presentation.common.TextWithDivider
import com.example.shikiflow.presentation.common.image.shimmerEffect
import com.example.shikiflow.presentation.common.mappers.BrowseTypeMapper.displayValue
import com.example.shikiflow.presentation.screen.browse.BrowseCardItem
import com.example.shikiflow.presentation.screen.browse.BrowseNavOptions
import com.example.shikiflow.presentation.screen.browse.main.NavigationSection
import com.example.shikiflow.presentation.screen.main.details.DetailsNavRoute
import com.example.shikiflow.presentation.viewmodel.browse.main.anilist.AnilistBrowseSectionUiState
import com.example.shikiflow.presentation.viewmodel.browse.main.anilist.AnilistBrowseViewModel
import com.example.shikiflow.utils.ignoreHorizontalParentPadding

@Composable
fun AnilistBrowseMainPage(
    paddingValues: PaddingValues,
    horizontalPadding: Dp,
    browseNavOptions: BrowseNavOptions,
    onIsAtTopChange: (Boolean) -> Unit,
    browseViewModel: AnilistBrowseViewModel = hiltViewModel()
) {
    val lazyListState = rememberLazyListState()
    val isAtTop by remember {
        derivedStateOf {
            lazyListState.firstVisibleItemIndex == 0 &&
            lazyListState.firstVisibleItemScrollOffset == 0
        }
    }
    var currentType by rememberSaveable { mutableStateOf(MediaType.ANIME) }

    val uiState by browseViewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(isAtTop) {
        onIsAtTopChange(isAtTop)
    }

    PullToRefreshCustomBox(
        isRefreshing = uiState.sections.any { it.value.isRefreshing },
        onRefresh = { browseViewModel.onRefresh() },
        enabled = isAtTop,
        modifier = Modifier
            .fillMaxSize()
            .padding(top = paddingValues.calculateTopPadding())
    ) {
        LazyColumn(
            state = lazyListState,
            contentPadding = PaddingValues(
                start = horizontalPadding,
                end = horizontalPadding,
                bottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding(),
                top = 8.dp
            ),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            item {
                NavigationSection(
                    currentType = currentType,
                    onMediaTypeChange = { currentType = it },
                    onNavigateSideScreen = { sideScreen ->
                        browseNavOptions.navigateToSideScreen(sideScreen)
                    }
                )
            }

            when(currentType) {
                MediaType.ANIME -> {
                    items(
                        items = BrowseType.AnimeBrowseType.alSections,
                        key = { browseType -> "anime_$browseType" }
                    ) { browseType ->
                        LaunchedEffect(browseType) {
                            browseViewModel.fetchBrowseData(browseType)
                        }

                        BrowseSection(
                            browseType = browseType,
                            sectionState = uiState.sections[browseType],
                            onSectionClick = { browseNavOptions.navigateToSideScreen(browseType) },
                            onRetryClick = { browseViewModel.onRetry(browseType) },
                            onItemClick = { id, mediaType ->
                                val detailsNavRoute = when(mediaType) {
                                    MediaType.ANIME -> DetailsNavRoute.AnimeDetails(id)
                                    MediaType.MANGA -> DetailsNavRoute.MangaDetails(id)
                                }

                                browseNavOptions.navigateToDetails(detailsNavRoute)
                            },
                            modifier = Modifier.animateItem()
                        )
                    }
                }
                MediaType.MANGA -> {
                    items(
                        items = BrowseType.MangaBrowseType.alSections,
                        key = { browseType -> "manga_$browseType" }
                    ) { browseType ->
                        LaunchedEffect(browseType) {
                            browseViewModel.fetchBrowseData(browseType)
                        }

                        BrowseSection(
                            browseType = browseType,
                            sectionState = uiState.sections[browseType],
                            onSectionClick = { browseNavOptions.navigateToSideScreen(browseType) },
                            onRetryClick = { browseViewModel.onRetry(browseType) },
                            onItemClick = { id, mediaType ->
                                val detailsNavRoute = when(mediaType) {
                                    MediaType.ANIME -> DetailsNavRoute.AnimeDetails(id)
                                    MediaType.MANGA -> DetailsNavRoute.MangaDetails(id)
                                }

                                browseNavOptions.navigateToDetails(detailsNavRoute)
                            },
                            modifier = Modifier.animateItem()
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun BrowseSection(
    browseType: BrowseType,
    sectionState: AnilistBrowseSectionUiState?,
    onSectionClick: () -> Unit,
    onRetryClick: () -> Unit,
    onItemClick: (Int, MediaType) -> Unit,
    modifier: Modifier = Modifier
) {
    val horizontalPadding = 12.dp
    val itemWidth = 144.dp

    sectionState?.let { uiState ->
        Column(
            modifier = modifier,
            verticalArrangement = Arrangement.spacedBy(4.dp, Alignment.Top)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextWithDivider(
                    text = stringResource(browseType.displayValue()),
                    style = MaterialTheme.typography.titleMedium
                )
                IconButton(
                    onClick = { onSectionClick() }
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = "Navigate to SideScreen"
                    )
                }
            }

            if(uiState.errorMessage != null) {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    ErrorItem(
                        message = stringResource(R.string.common_error),
                        buttonLabel = stringResource(R.string.common_retry),
                        onButtonClick = { onRetryClick() }
                    )
                }
            } else {
                SnapFlingLazyRow(
                    modifier = Modifier
                        .ignoreHorizontalParentPadding(horizontalPadding)
                        .fillMaxWidth(),
                    contentPadding = PaddingValues(horizontal = horizontalPadding),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if(uiState.isLoading) {
                        items(12) {
                            Box(
                                modifier = Modifier
                                    .width(itemWidth)
                                    .aspectRatio(2f / 2.85f)
                                    .clip(RoundedCornerShape(12.dp))
                                    .shimmerEffect()
                            )
                        }
                    } else {
                        items(uiState.browseMedia.size) { index ->
                            BrowseCardItem(
                                browseItem = uiState.browseMedia[index],
                                onItemClick = onItemClick,
                                modifier = Modifier.width(itemWidth)
                            )
                        }
                    }
                }
            }
        }
    }
}