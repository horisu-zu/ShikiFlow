package com.example.shikiflow.presentation.screen.main

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
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
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.shikiflow.R
import com.example.shikiflow.domain.model.tracks.MediaType
import com.example.shikiflow.presentation.common.ErrorItem
import com.example.shikiflow.presentation.screen.main.details.MediaNavOptions
import com.example.shikiflow.presentation.screen.browse.BrowseGridItem
import com.example.shikiflow.presentation.viewmodel.SimilarMediaViewModel
import com.example.shikiflow.utils.Resource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SimilarMediaScreen(
    mediaTitle: String,
    mediaId: String,
    mediaType: MediaType,
    navOptions: MediaNavOptions,
    similarMediaViewModel: SimilarMediaViewModel = hiltViewModel()
) {
    val lazyGridState = rememberLazyGridState()
    val similarMediaState by similarMediaViewModel.similarMedia.collectAsStateWithLifecycle()

    val horizontalPadding = 12.dp
    val isAtTop by remember {
        derivedStateOf {
            lazyGridState.firstVisibleItemIndex == 0 &&
            lazyGridState.firstVisibleItemScrollOffset == 0
        }
    }

    LaunchedEffect(mediaId) {
        similarMediaViewModel.getSimilarMedia(mediaId, mediaType)
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
        if(similarMediaState is Resource.Loading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) { CircularProgressIndicator() }
        } else if(similarMediaState is Resource.Error) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                ErrorItem(
                    message = stringResource(R.string.similar_media_error),
                    buttonLabel = stringResource(R.string.common_error)
                )
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(96.dp),
                state = lazyGridState,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        top = innerPadding.calculateTopPadding(),
                        start = innerPadding.calculateStartPadding(LayoutDirection.Ltr),
                        end = innerPadding.calculateEndPadding(LayoutDirection.Ltr)
                    )
                    .padding(horizontal = horizontalPadding),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                similarMediaState.data?.let { mediaList ->
                    items(mediaList.size) { index ->
                        val browseItem = mediaList[index]

                        BrowseGridItem(
                            browseItem = browseItem,
                            onItemClick = { id, mediaType ->
                                when(mediaType) {
                                    MediaType.ANIME -> navOptions.navigateToAnimeDetails(id)
                                    MediaType.MANGA -> navOptions.navigateToMangaDetails(id)
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}