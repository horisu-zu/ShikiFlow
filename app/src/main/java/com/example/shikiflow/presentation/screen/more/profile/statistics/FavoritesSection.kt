package com.example.shikiflow.presentation.screen.more.profile.statistics

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.example.shikiflow.presentation.common.mappers.FavoriteCategoryMapper.displayValue
import com.example.shikiflow.presentation.common.mappers.FavoriteCategoryMapper.toTabRowItem
import com.example.shikiflow.presentation.viewmodel.user.UserFavoritesViewModel

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun FavoritesSection(
    userId: String,
    favoriteCategories: List<FavoriteCategory>,
    horizontalPadding: Dp,
    onFavoriteClick: (Int, FavoriteCategory) -> Unit,
    modifier: Modifier = Modifier,
    userFavoritesViewModel: UserFavoritesViewModel = hiltViewModel()
) {
    val lazyGridState = rememberLazyGridState()

    var currentFavorite by rememberSaveable { mutableStateOf(favoriteCategories.first()) }
    val userFavoriteItems = userFavoritesViewModel.loadUserFavorites(
        userId = userId,
        favoriteCategory = currentFavorite
    ).collectAsLazyPagingItems()

    Scaffold(
        modifier = modifier,
        topBar = {
            FavoritesSectionHeader(
                currentFavorite = currentFavorite,
                favoriteCategories = favoriteCategories,
                onFavoriteClick = { favorite -> currentFavorite = favorite },
                paddingValues = PaddingValues(
                    start = 12.dp,
                    end = 12.dp,
                    bottom = 8.dp
                ),
                modifier = Modifier.fillMaxWidth()
            )
        }
    ) { paddingValues ->
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
                    state = lazyGridState,
                    columns = GridCells.Adaptive(108.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp, Alignment.Top),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    contentPadding = PaddingValues(
                        start = horizontalPadding,
                        end = horizontalPadding,
                        top = paddingValues.calculateTopPadding() + 12.dp,
                        bottom = 12.dp
                    ),
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)
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
                                    onFavoriteClick(id, currentFavorite)
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
                        } else if (userFavoriteItems.loadState.append is LoadState.Loading) {
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
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun FavoritesSectionHeader(
    currentFavorite: FavoriteCategory,
    favoriteCategories: List<FavoriteCategory>,
    onFavoriteClick: (FavoriteCategory) -> Unit,
    paddingValues: PaddingValues,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(
                shape = RoundedCornerShape(
                    bottomEndPercent = 24,
                    bottomStartPercent = 24
                )
            )
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(paddingValues),
        verticalArrangement = Arrangement.spacedBy(4.dp, Alignment.Top),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ConnectedButtonGroup(
            items = favoriteCategories.map { it.toTabRowItem() },
            selectedIndex = favoriteCategories.indexOf(currentFavorite),
            onItemSelection = { index ->
                onFavoriteClick(favoriteCategories[index])
            }
        )
        Text(
            text = stringResource(currentFavorite.displayValue()),
            style = MaterialTheme.typography.titleMedium
        )
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