package com.example.shikiflow.presentation.screen.main.details.anime.watch

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.shikiflow.domain.model.tracks.MediaType
import com.example.shikiflow.presentation.screen.main.details.anime.watch.player.EpisodeMetadata
import com.example.shikiflow.presentation.screen.main.details.manga.read.MediaItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EpisodeSelectionScreen(
    title: String,
    translationGroup: String,
    episodesRange: IntRange,
    link: String,
    completedEpisodes: Int,
    navOptions: AnimeWatchNavOptions
) {
    val lazyListState = rememberLazyListState()
    val isAtTop by remember {
        derivedStateOf {
            lazyListState.firstVisibleItemIndex == 0 && lazyListState.firstVisibleItemScrollOffset == 0
        }
    }

    val episodesList = remember(episodesRange) {
        episodesRange.toList()
    }

    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = {
                        Text(
                            text = title,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            style = MaterialTheme.typography.titleMedium
                        )
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = { navOptions.navigateBack() }
                        ) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back to Main"
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = if(isAtTop) MaterialTheme.colorScheme.background
                            else MaterialTheme.colorScheme.surfaceContainer
                    )
                )
                HorizontalDivider()
            }
        }, modifier = Modifier.fillMaxSize()
    ) { paddingValues ->
        LazyColumn(
            state = lazyListState,
            modifier = Modifier
                .fillMaxSize()
                .padding(top = paddingValues.calculateTopPadding()),
            contentPadding = PaddingValues(
                start = 8.dp,
                end = 8.dp,
                top = 6.dp,
                bottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
            ),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items(episodesList) { episodeNum ->
                MediaItem(
                    mediaNumber = episodeNum.toString(),
                    onItemClick = {
                        navOptions.navigateToEpisodeScreen(
                            playerNavigate = EpisodeMetadata(
                                link,
                                translationGroup,
                                episodeNum,
                                firstEpisode = episodesRange.first,
                                lastEpisode = episodesRange.last
                            )
                        )
                    },
                    isCompleted = episodeNum <= completedEpisodes,
                    mediaType = MediaType.ANIME
                )
            }
        }
    }
}