package com.example.shikiflow.presentation.screen.more.profile.favorites

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.shikiflow.R
import com.example.shikiflow.domain.model.auth.AuthType
import com.example.shikiflow.domain.model.media_details.MediaTitle.Companion.preferred
import com.example.shikiflow.domain.model.media_details.PreferredTitleType
import com.example.shikiflow.domain.model.user.FavoriteCategory
import com.example.shikiflow.domain.model.user.UserFavorite
import com.example.shikiflow.presentation.common.ConnectedButtonGroup
import com.example.shikiflow.presentation.common.ErrorItem
import com.example.shikiflow.presentation.common.PullToRefreshCustomBox
import com.example.shikiflow.presentation.common.image.BaseImage
import com.example.shikiflow.presentation.common.image.ImageType
import com.example.shikiflow.presentation.common.mappers.ProfileMapper.toTabRowItem
import com.example.shikiflow.presentation.common.shimmerEffect
import com.example.shikiflow.presentation.screen.main.LocalTitleTypeController
import com.example.shikiflow.presentation.viewmodel.user.favorites.FavoritesViewModel
import kotlinx.coroutines.launch
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyGridState

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun FavoritesSection(
    userId: Int,
    favoriteCategories: List<FavoriteCategory>,
    isRefreshEnabled: Boolean,
    horizontalPadding: Dp,
    onRefresh: () -> Unit,
    onFavoriteClick: (FavoriteCategory, Int) -> Unit,
    onStudioClick: (Int, String) -> Unit,
    favoritesViewModel: FavoritesViewModel = hiltViewModel()
) {
    val params by favoritesViewModel.params.collectAsStateWithLifecycle()
    val titleType = LocalTitleTypeController.current

    val pagerState = rememberPagerState(
        initialPage = params.currentCategory?.let { favoriteCategory ->
            favoriteCategories.indexOf(favoriteCategory).let { index ->
                if(index != -1) index else 0
            }
        } ?: 0,
        pageCount = { favoriteCategories.size }
    )
    val scope = rememberCoroutineScope()
    val isEditingOrder = remember { mutableStateOf(false) }

    LaunchedEffect(userId) {
        favoritesViewModel.setUserId(userId)
    }

    LaunchedEffect(pagerState.currentPage) {
        favoritesViewModel.setCategory(
            favoriteCategory = favoriteCategories[pagerState.currentPage]
        )
    }

    Scaffold(
        topBar = {
            ConnectedButtonGroup(
                items = favoriteCategories.map { it.toTabRowItem() },
                selectedIndex = pagerState.currentPage,
                onItemSelection = { index ->
                    scope.launch {
                        pagerState.animateScrollToPage(
                            page = index,
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioNoBouncy,
                                stiffness = Spring.StiffnessMediumLow
                            )
                        )
                    }
                },
                enabled = !isEditingOrder.value,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        start = horizontalPadding,
                        end = horizontalPadding,
                        bottom = 4.dp
                    ),
                contentPadding = PaddingValues(
                    horizontal = 8.dp,
                    vertical = 4.dp
                )
            )
        }
    ) { paddingValues ->
        HorizontalPager(
            state = pagerState,
            userScrollEnabled = !isEditingOrder.value
        ) { page ->
            val lazyGridState = rememberLazyGridState()
            val userFavoriteItems = favoritesViewModel.userFavorites[favoriteCategories[page]]
                ?.collectAsLazyPagingItems() ?: return@HorizontalPager
            val imageType = ImageType.Poster(width = Int.MAX_VALUE.dp)

            LaunchedEffect(Unit) {
                favoritesViewModel.orderEvent.collect {
                    isEditingOrder.value = false
                    userFavoriteItems.refresh()
                }
            }

            when (userFavoriteItems.loadState.refresh) {
                is LoadState.Error -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        ErrorItem(
                            message = stringResource(R.string.common_error),
                            buttonLabel = stringResource(R.string.common_retry),
                            onButtonClick = { userFavoriteItems.refresh() }
                        )
                    }
                }
                else -> {
                    when (isEditingOrder.value) {
                        true -> {
                            OrderEditorComponent(
                                lazyGridState = lazyGridState,
                                userFavoriteItems = userFavoriteItems,
                                titleType = titleType,
                                imageType = imageType,
                                horizontalPadding = horizontalPadding,
                                onClearClick = { isEditingOrder.value = false },
                                onSaveClick = { reorderedIds ->
                                    favoritesViewModel.changeOrder(reorderedIds, favoriteCategories[page])
                                },
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(top = paddingValues.calculateTopPadding())
                            )
                        }
                        false -> {
                            PullToRefreshCustomBox(
                                isRefreshing = false,
                                enabled = isRefreshEnabled,
                                onRefresh = {
                                    onRefresh()
                                    userFavoriteItems.refresh()
                                },
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(top = paddingValues.calculateTopPadding())
                            ) {
                                LazyVerticalGrid(
                                    state = lazyGridState,
                                    columns = GridCells.Adaptive(108.dp),
                                    userScrollEnabled = userFavoriteItems.loadState.refresh !is LoadState.Loading,
                                    verticalArrangement = Arrangement.spacedBy(6.dp, Alignment.Top),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    contentPadding = PaddingValues(
                                        horizontal = horizontalPadding,
                                        vertical = 8.dp
                                    ),
                                    modifier = Modifier.fillMaxSize()
                                ) {
                                    if (userFavoriteItems.loadState.refresh is LoadState.Loading) {
                                        items(24) { index ->
                                            if (favoriteCategories[page] == FavoriteCategory.STUDIO) {
                                                FavoriteStudioItemPlaceholder(
                                                    itemIndex = index,
                                                    modifier = Modifier.aspectRatio(1.5f)
                                                )
                                            } else {
                                                FavoriteItemPlaceholder(
                                                    imageType = imageType
                                                )
                                            }
                                        }
                                    } else if (userFavoriteItems.loadState.refresh is LoadState.NotLoading) {
                                        items(
                                            count = userFavoriteItems.itemCount,
                                            key = { index -> userFavoriteItems[index]?.id ?: index }
                                        ) { index ->
                                            userFavoriteItems[index]?.let { item ->
                                                if(item.favoriteCategory == FavoriteCategory.STUDIO) {
                                                    FavoriteStudioItem(
                                                        name = item.name.preferred(titleType),
                                                        modifier = Modifier
                                                            .fillMaxWidth()
                                                            .aspectRatio(1.5f)
                                                            .clickable {
                                                                onStudioClick(
                                                                    item.id,
                                                                    item.name.preferred(titleType)
                                                                )
                                                            }
                                                    )
                                                } else {
                                                    FavoriteItem(
                                                        userFavorite = item,
                                                        titleType = titleType,
                                                        imageType = imageType,
                                                        modifier = Modifier
                                                            .fillMaxWidth()
                                                            .clip(imageType.shape)
                                                            .combinedClickable(
                                                                onClick = {
                                                                    onFavoriteClick(favoriteCategories[page], item.id)
                                                                },
                                                                onLongClick = {
                                                                    if (params.authType == AuthType.ANILIST) {
                                                                        isEditingOrder.value = true
                                                                    }
                                                                }
                                                            )
                                                    )
                                                }
                                            }
                                        }
                                    }

                                    item(span = { GridItemSpan(maxLineSpan) }) {
                                        if (userFavoriteItems.loadState.append is LoadState.Loading) {
                                            Box(
                                                modifier = Modifier.fillMaxWidth(),
                                                contentAlignment = Alignment.Center
                                            ) { CircularProgressIndicator() }
                                        } else if (userFavoriteItems.loadState.append is LoadState.Error) {
                                            Box(
                                                modifier = Modifier.fillMaxSize(),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                ErrorItem(
                                                    message = stringResource(R.string.common_error),
                                                    buttonLabel = stringResource(R.string.common_retry),
                                                    onButtonClick = { userFavoriteItems.retry() }
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun OrderEditorComponent(
    lazyGridState: LazyGridState,
    userFavoriteItems: LazyPagingItems<UserFavorite>,
    titleType: PreferredTitleType,
    imageType: ImageType,
    horizontalPadding: Dp,
    onClearClick: () -> Unit,
    onSaveClick: (List<Int>) -> Unit,
    modifier: Modifier = Modifier
) {
    val hapticFeedback = LocalHapticFeedback.current
    var reorderableList by remember(userFavoriteItems.itemSnapshotList.items) {
        mutableStateOf(userFavoriteItems.itemSnapshotList.items)
    }
    val reorderableState = rememberReorderableLazyGridState(lazyGridState) { from, to ->
        reorderableList = reorderableList.toMutableList().apply {
            add(to.index, removeAt(from.index))
        }

        hapticFeedback.performHapticFeedback(HapticFeedbackType.SegmentFrequentTick)
    }

    Box {
        LazyVerticalGrid(
            state = lazyGridState,
            columns = GridCells.Adaptive(108.dp),
            userScrollEnabled = userFavoriteItems.loadState.refresh !is LoadState.Loading,
            verticalArrangement = Arrangement.spacedBy(6.dp, Alignment.Top),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            contentPadding = PaddingValues(
                start = horizontalPadding,
                end = horizontalPadding,
                top = 8.dp,
                bottom = 64.dp
            ),
            modifier = modifier
        ) {
            items(
                items = reorderableList,
                key = { item -> item.id }
            ) { item ->
                ReorderableItem(reorderableState, key = item.id) { isDragging ->
                    val backgroundColor by animateColorAsState(
                        targetValue = if (isDragging) MaterialTheme.colorScheme.surfaceContainerHigh
                            else MaterialTheme.colorScheme.background,
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioNoBouncy,
                            stiffness = Spring.StiffnessMediumLow
                        )
                    )
                    val dragHandleModifier = Modifier
                        .draggableHandle(
                            onDragStarted = {
                                hapticFeedback.performHapticFeedback(HapticFeedbackType.GestureThresholdActivate)
                            },
                            onDragStopped = {
                                hapticFeedback.performHapticFeedback(HapticFeedbackType.GestureEnd)
                            }
                        )

                    if (item.favoriteCategory == FavoriteCategory.STUDIO) {
                        FavoriteStudioItem(
                            name = item.name.preferred(titleType),
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(1.5f)
                                .then(dragHandleModifier)
                        )
                    } else {
                        FavoriteItem(
                            userFavorite = item,
                            titleType = titleType,
                            imageType = imageType,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(imageType.shape)
                                .background(backgroundColor)
                                .then(dragHandleModifier)
                        )
                    }
                }
            }
        }

        Row(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(all = 16.dp)
                .background(
                    color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(percent = 32)
                )
                .padding(horizontal = 8.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                shape = RoundedCornerShape(percent = 24),
                onClick = onClearClick,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Clear,
                    contentDescription = "Clear Order Changes",
                    modifier = Modifier.size(24.dp)
                )
            }

            IconButton(
                enabled = reorderableList != userFavoriteItems.itemSnapshotList.items,
                shape = RoundedCornerShape(percent = 24),
                onClick = { onSaveClick(reorderableList.map { it.id }) } ,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Save Order Changes",
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@Composable
private fun FavoriteItem(
    userFavorite: UserFavorite,
    titleType: PreferredTitleType,
    imageType: ImageType,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        BaseImage(
            model = userFavorite.imageUrl,
            contentScale = ContentScale.Crop,
            imageType = imageType
        )
        Text(
            text = userFavorite.name.preferred(titleType),
            style = MaterialTheme.typography.labelSmall,
            overflow = TextOverflow.Ellipsis,
            maxLines = 2,
            modifier = Modifier.padding(all = 4.dp)
        )
    }
}

@Composable
private fun FavoriteItemPlaceholder(
    imageType: ImageType,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.shimmerEffect(overContent = true)
    ) {
        Box(
            modifier = Modifier
                .width(imageType.width)
                .aspectRatio(imageType.aspectRatio)
                .clip(imageType.shape)
                .background(MaterialTheme.colorScheme.onSurface)
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(all = 4.dp)
                .height(MaterialTheme.typography.labelSmall.lineHeight.value.dp)
                .clip(RoundedCornerShape(percent = 32))
                .background(MaterialTheme.colorScheme.onSurface)
        )
    }
}

@Composable
private fun FavoriteStudioItem(
    name: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .then(modifier)
            .padding(horizontal = 12.dp, vertical = 18.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = name,
            style = MaterialTheme.typography.bodyMedium.copy(
                textAlign = TextAlign.Center
            ),
            overflow = TextOverflow.Ellipsis,
            maxLines = 2
        )
    }
}

@Composable
private fun FavoriteStudioItemPlaceholder(
    itemIndex: Int,
    modifier: Modifier = Modifier,
    maxValue: Int = 4
) {
    val indexValue = itemIndex % maxValue + 1

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .padding(horizontal = 12.dp, vertical = 18.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        repeat((maxValue / indexValue).coerceAtMost(2)) { index ->
            Box(
                modifier = Modifier
                    .width(40.dp * (indexValue + index))
                    .height(MaterialTheme.typography.bodyMedium.lineHeight.value.dp)
                    .clip(RoundedCornerShape(percent = 32))
                    .shimmerEffect()
            )
        }
    }
}