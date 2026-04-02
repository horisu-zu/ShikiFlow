package com.example.shikiflow.presentation.screen.browse.main

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.animateFloatingActionButton
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.shikiflow.R
import com.example.shikiflow.domain.model.browse.Browse
import com.example.shikiflow.domain.model.browse.BrowseMedia
import com.example.shikiflow.domain.model.media_details.MediaPersonShort
import com.example.shikiflow.domain.model.search.MediaBrowseOptions
import com.example.shikiflow.domain.model.tracks.MediaType
import com.example.shikiflow.presentation.common.ErrorItem
import com.example.shikiflow.presentation.common.SnapFlingLazyRow
import com.example.shikiflow.presentation.common.image.BaseImage
import com.example.shikiflow.presentation.common.image.ImageType
import com.example.shikiflow.presentation.common.mappers.SearchTypeMapper.displayValue
import com.example.shikiflow.presentation.screen.browse.BrowseGridItem
import com.example.shikiflow.presentation.screen.browse.BrowseNavOptions
import com.example.shikiflow.presentation.screen.main.details.DetailsNavRoute
import com.example.shikiflow.presentation.screen.more.profile.social.UserSocialItem
import com.example.shikiflow.presentation.viewmodel.browse.search.BrowseSearchViewModel
import com.example.shikiflow.utils.ignoreHorizontalParentPadding

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun BrowseSearchBar(
    isAtMainTop: Boolean,
    navOptions: BrowseNavOptions,
    horizontalPadding: Dp,
    modifier: Modifier = Modifier,
    searchViewModel: BrowseSearchViewModel = hiltViewModel()
) {
    val searchState by searchViewModel.searchState.collectAsStateWithLifecycle()

    val animatedHorizontalPadding by animateDpAsState(
        targetValue = if (!searchState.isSearchActive) horizontalPadding
            else 0.dp
    )

    Column(modifier = modifier) {
        SearchBar(
            inputField = {
                when(searchState.isSearchActive) {
                    true -> {
                        SearchBarDefaults.InputField(
                            query = searchState.query,
                            onQueryChange = { query ->
                                searchViewModel.onQueryChange(query)
                            },
                            onSearch = { /**/ },
                            expanded = true,
                            onExpandedChange = { isExpanded ->
                                searchViewModel.onSearchStateChange(isExpanded)
                            },
                            placeholder = {
                                Text(
                                    text = stringResource(R.string.browse_page_search)
                                )
                            },
                            leadingIcon = {
                                IconButton(
                                    onClick = { searchViewModel.onSearchStateChange(false) }
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Default.KeyboardArrowLeft,
                                        contentDescription = "Exit Search"
                                    )
                                }
                            },
                            trailingIcon = {
                                if(searchState.query.isNotEmpty()) {
                                    IconButton(
                                        onClick = { searchViewModel.onQueryChange("") }
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Clear,
                                            contentDescription = "Clear Query"
                                        )
                                    }
                                }
                            }
                        )
                    }
                    false -> {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = stringResource(R.string.bottom_nav_item_browse),
                                style = MaterialTheme.typography.headlineSmall
                            )
                            IconButton(
                                onClick = { searchViewModel.onSearchStateChange(true) }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = "Search"
                                )
                            }
                        }
                    }
                }
            },
            expanded = searchState.isSearchActive,
            onExpandedChange = { isExpanded ->
                searchViewModel.onSearchStateChange(isExpanded)
            },
            colors = SearchBarDefaults.colors(
                containerColor = if(!searchState.isSearchActive) Color.Transparent
                    else MaterialTheme.colorScheme.surfaceContainer
            ),
            modifier = Modifier
                .background(
                    color = if(!isAtMainTop) {
                        MaterialTheme.colorScheme.surfaceContainer
                    } else MaterialTheme.colorScheme.background
                )
                .padding(horizontal = animatedHorizontalPadding)
        ) {
            SearchBarContent(
                navOptions = navOptions,
                horizontalPadding = horizontalPadding
            )
        }

        if(!isAtMainTop) {
            HorizontalDivider()
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun SearchBarContent(
    navOptions: BrowseNavOptions,
    horizontalPadding: Dp,
    modifier: Modifier = Modifier,
    searchViewModel: BrowseSearchViewModel = hiltViewModel()
) {
    val configuration = LocalWindowInfo.current
    val authType by searchViewModel.authType.collectAsStateWithLifecycle()
    val searchParams by searchViewModel.params.collectAsStateWithLifecycle()

    val browseItems = searchViewModel.browseItems.collectAsLazyPagingItems()

    var showBottomSheet by remember { mutableStateOf(false) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                containerColor = MaterialTheme.colorScheme.surfaceContainer,
                onClick = { showBottomSheet = true },
                modifier = Modifier
                    .imePadding()
                    .animateFloatingActionButton(
                        visible = searchParams.searchType == SearchType.MEDIA,
                        alignment = Alignment.BottomCenter
                    )
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_sort),
                    contentDescription = "Show Filters"
                )
            }
        },
        contentWindowInsets = WindowInsets(0),
        modifier = modifier
    ) { paddingValues ->
        PullToRefreshBox(
            isRefreshing = browseItems.loadState.refresh is LoadState.Loading,
            onRefresh = { browseItems.refresh() },
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            LazyVerticalGrid(
                columns = when (searchParams.searchType) {
                    SearchType.USER -> GridCells.Adaptive(180.dp)
                    else -> GridCells.Adaptive(108.dp)
                },
                contentPadding = PaddingValues(
                    start = horizontalPadding,
                    end = horizontalPadding,
                    bottom = WindowInsets.navigationBars
                        .asPaddingValues()
                        .calculateBottomPadding()
                ),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                stickyHeader {
                    SnapFlingLazyRow(
                        modifier = Modifier
                            .ignoreHorizontalParentPadding(horizontalPadding)
                            .fillMaxWidth()
                            .clip(
                                shape = RoundedCornerShape(
                                    bottomStart = 16.dp,
                                    bottomEnd = 16.dp
                                )
                            )
                            .background(
                                color = MaterialTheme.colorScheme.surfaceContainer
                            )
                            .padding(vertical = 4.dp),
                        contentPadding = PaddingValues(horizontal = horizontalPadding),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(SearchType.entries) { searchType ->
                            val isSelected = searchParams.searchType == searchType

                            FilterChip(
                                selected = isSelected,
                                onClick = {
                                    searchViewModel.setSearchType(searchType)
                                },
                                label = {
                                    Text(
                                        text = stringResource(id = searchType.displayValue())
                                    )
                                },
                                leadingIcon = if (isSelected) {
                                    {
                                        Icon(
                                            imageVector = Icons.Default.Done,
                                            contentDescription = null
                                        )
                                    }
                                } else { null },
                                colors = FilterChipDefaults.filterChipColors(
                                    containerColor = MaterialTheme.colorScheme.background
                                )
                            )
                        }
                    }
                }

                if(browseItems.loadState.refresh is LoadState.Error) {
                    item(span = { GridItemSpan(maxCurrentLineSpan) }) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(configuration.containerDpSize.height * 0.8f),
                            contentAlignment = Alignment.Center
                        ) {
                            ErrorItem(
                                message = stringResource(R.string.b_mss_error),
                                buttonLabel = stringResource(id = R.string.common_retry),
                                onButtonClick = { browseItems.refresh() }
                            )
                        }
                    }
                } else if(browseItems.loadState.refresh is LoadState.NotLoading) {
                    items(browseItems.itemCount) { index ->
                        browseItems[index]?.let { browseItem ->
                            when(browseItem) {
                                is BrowseMedia -> {
                                    BrowseGridItem(
                                        browseItem = browseItem,
                                        onItemClick = { id, mediaType ->
                                            val detailsNavRoute = when(mediaType) {
                                                MediaType.ANIME -> DetailsNavRoute.AnimeDetails(id)
                                                MediaType.MANGA -> DetailsNavRoute.MangaDetails(id)
                                            }

                                            navOptions.navigateToDetails(detailsNavRoute)
                                        }
                                    )
                                }
                                is Browse.Character -> {
                                    MediaPersonItem(
                                        mediaPerson = browseItem.data,
                                        onItemClick = { id ->
                                            navOptions.navigateToDetails(
                                                DetailsNavRoute.CharacterDetails(id)
                                            )
                                        }
                                    )
                                }
                                is Browse.Staff -> {
                                    MediaPersonItem(
                                        mediaPerson = browseItem.data,
                                        onItemClick = { id ->
                                            navOptions.navigateToDetails(
                                                DetailsNavRoute.Staff(id)
                                            )
                                        }
                                    )
                                }
                                is Browse.User -> {
                                    UserSocialItem(
                                        user = browseItem.data,
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(12.dp))
                                            .clickable {
                                                navOptions.navigateToProfile(
                                                    user = browseItem.data
                                                )
                                            }
                                            .padding(horizontal = 8.dp, vertical = 6.dp)
                                    )
                                }
                            }
                        }
                    }

                    item(span = { GridItemSpan(maxCurrentLineSpan) }) {
                        browseItems.apply {
                            if (loadState.append is LoadState.Loading) {
                                Box(
                                    modifier = Modifier.fillMaxWidth(),
                                    contentAlignment = Alignment.Center
                                ) { CircularProgressIndicator() }
                            } else if (loadState.append is LoadState.Error) {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    ErrorItem(
                                        message = stringResource(R.string.common_error),
                                        buttonLabel = stringResource(R.string.common_retry),
                                        onButtonClick = { browseItems.retry() }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        if (showBottomSheet) {
            authType?.let { authType ->
                SearchBottomSheet(
                    authType = authType,
                    searchOptions = searchParams.mediaBrowseOptions,
                    onOptionsChanged = { newOptions -> searchViewModel.updateSearchOptions(newOptions) },
                    onTypeChanged = { newType ->
                        searchViewModel.updateSearchOptions(MediaBrowseOptions(newType))
                    },
                    onDismiss = { showBottomSheet = false }
                )
            }
        }
    }
}

@Composable
private fun MediaPersonItem(
    mediaPerson: MediaPersonShort,
    onItemClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable { onItemClick(mediaPerson.id) }
    ) {
        BaseImage(
            model = mediaPerson.imageUrl,
            imageType = ImageType.Poster(
                defaultWidth = Int.MAX_VALUE.dp
            )
        )

        Text(
            text = mediaPerson.fullName,
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(
                horizontal = 4.dp,
                vertical = 2.dp
            )
        )
    }
}