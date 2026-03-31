package com.example.shikiflow.presentation.screen.main.details.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.shikiflow.R
import com.example.shikiflow.domain.model.tracks.MediaType
import com.example.shikiflow.presentation.common.ErrorItem
import com.example.shikiflow.presentation.screen.main.details.MediaNavOptions
import com.example.shikiflow.presentation.screen.browse.BrowseGridItem
import com.example.shikiflow.presentation.viewmodel.media.similar.SimilarMediaViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SimilarMediaScreen(
    mediaTitle: String,
    mediaId: Int,
    mediaType: MediaType,
    navOptions: MediaNavOptions,
    similarMediaViewModel: SimilarMediaViewModel = hiltViewModel()
) {
    val lazyGridState = rememberLazyGridState()
    val similarMediaState = similarMediaViewModel.similarMediaFlow.collectAsLazyPagingItems()

    val horizontalPadding = 12.dp
    val isAtTop by remember {
        derivedStateOf {
            lazyGridState.firstVisibleItemIndex == 0 &&
            lazyGridState.firstVisibleItemScrollOffset == 0
        }
    }

    LaunchedEffect(mediaId) {
        similarMediaViewModel.setMediaParams(mediaId, mediaType)
    }

    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = {
                        Column(
                            verticalArrangement = Arrangement.SpaceEvenly
                        ) {
                            Text(
                                text = stringResource(R.string.similar_media_app_bar_label),
                                style = MaterialTheme.typography.bodyLarge
                            )
                            Text(
                                text = mediaTitle,
                                style = MaterialTheme.typography.labelMedium.copy(
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.75f)
                                ),
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = { navOptions.navigateBack() }
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = null
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.background
                    )
                )
                if(!isAtTop) { HorizontalDivider() }
            }
        }
    ) { innerPadding ->
        when (similarMediaState.loadState.refresh) {
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
                        message = (similarMediaState.loadState.refresh as LoadState.Error)
                            .error.message ?: stringResource(R.string.similar_media_error),
                        buttonLabel = stringResource(R.string.common_retry),
                        onButtonClick = { similarMediaState.retry() }
                    )
                }
            }
            else -> {
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(96.dp),
                    state = lazyGridState,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = innerPadding.calculateTopPadding()),
                    contentPadding = PaddingValues(
                        start = horizontalPadding,
                        end = horizontalPadding,
                        top = 12.dp,
                        bottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
                    ),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    items(
                        count = similarMediaState.itemCount,
                        key = { index -> similarMediaState[index]?.id ?: index }
                    ) { index ->
                        similarMediaState[index]?.let { mediaRecommendation ->
                            BrowseGridItem(
                                browseItem = mediaRecommendation,
                                onItemClick = { id, mediaType ->
                                    when (mediaType) {
                                        MediaType.ANIME -> navOptions.navigateToAnimeDetails(id)
                                        MediaType.MANGA -> navOptions.navigateToMangaDetails(id)
                                    }
                                }
                            )
                        }
                    }
                    similarMediaState.apply {
                        if (loadState.append is LoadState.Loading) {
                            item(span = { GridItemSpan(maxLineSpan) }) {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) { CircularProgressIndicator() }
                            }
                        }
                        if (loadState.append is LoadState.Error) {
                            item(span = { GridItemSpan(maxLineSpan) }) {
                                ErrorItem(
                                    message = stringResource(R.string.common_error),
                                    showFace = false,
                                    buttonLabel = stringResource(R.string.common_retry),
                                    onButtonClick = { similarMediaState.retry() }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}