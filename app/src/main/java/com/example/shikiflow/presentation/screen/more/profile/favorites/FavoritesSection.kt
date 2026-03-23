package com.example.shikiflow.presentation.screen.more.profile.favorites

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.shikiflow.R
import com.example.shikiflow.domain.model.user.FavoriteCategory
import com.example.shikiflow.presentation.common.ConnectedButtonGroup
import com.example.shikiflow.presentation.common.ErrorItem
import com.example.shikiflow.presentation.common.image.BaseImage
import com.example.shikiflow.presentation.common.image.ImageType
import com.example.shikiflow.presentation.common.mappers.ProfileMapper.toTabRowItem
import com.example.shikiflow.presentation.viewmodel.user.favorites.FavoritesViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun FavoritesSection(
    userId: String,
    favoriteCategories: List<FavoriteCategory>,
    horizontalPadding: Dp,
    onFavoriteClick: (Int, FavoriteCategory) -> Unit,
    favoritesViewModel: FavoritesViewModel = hiltViewModel()
) {
    val pagerState = rememberPagerState { favoriteCategories.size }
    val scope = rememberCoroutineScope()

    LaunchedEffect(userId) {
        favoritesViewModel.setUserId(userId.toInt())
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
            state = pagerState
        ) { page ->
            val userFavoriteItems = favoritesViewModel.userFavorites[favoriteCategories[page]]
                ?.collectAsLazyPagingItems() ?: return@HorizontalPager

            when (userFavoriteItems.loadState.refresh) {
                is LoadState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) { CircularProgressIndicator() }
                }
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
                    LazyVerticalGrid(
                        columns = GridCells.Adaptive(108.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp, Alignment.Top),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        contentPadding = PaddingValues(
                            horizontal = horizontalPadding,
                            vertical = 8.dp
                        ),
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(top = paddingValues.calculateTopPadding())
                    ) {
                        items(
                            count = userFavoriteItems.itemCount,
                            key = { index -> userFavoriteItems[index]?.id ?: index }
                        ) { index ->
                            userFavoriteItems[index]?.let { item ->
                                FavoriteMediaItem(
                                    id = item.id,
                                    title = item.name,
                                    imageUrl = item.imageUrl,
                                    onItemClick = { id ->
                                        onFavoriteClick(id, favoriteCategories[page])
                                    },
                                    modifier = Modifier.animateItem()
                                )
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

@Composable
private fun FavoriteMediaItem(
    id: Int,
    title: String,
    imageUrl: String?,
    onItemClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clickable(
                onClick = { onItemClick(id) },
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ),
        verticalArrangement = Arrangement.spacedBy(4.dp, Alignment.Top)
    ) {
        BaseImage(
            model = imageUrl,
            contentScale = ContentScale.Crop,
            imageType = ImageType.Poster(
                defaultWidth = Int.MAX_VALUE.dp,
            )
        )
        Text(
            text = title,
            style = MaterialTheme.typography.labelSmall,
            overflow = TextOverflow.Ellipsis,
            maxLines = 2
        )
    }
}