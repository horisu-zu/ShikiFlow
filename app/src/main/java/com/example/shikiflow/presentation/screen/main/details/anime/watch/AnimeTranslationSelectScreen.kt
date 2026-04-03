package com.example.shikiflow.presentation.screen.main.details.anime.watch

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilterChip
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.shikiflow.R
import com.example.shikiflow.domain.model.kodik.KodikAnime
import com.example.shikiflow.presentation.common.ErrorItem
import com.example.shikiflow.presentation.common.PullToRefreshCustomBox
import com.example.shikiflow.presentation.screen.main.details.anime.watch.player.EpisodeMetadata
import com.example.shikiflow.presentation.viewmodel.anime.watch.translations.AnimeTranslationsViewModel
import com.example.shikiflow.utils.Converter
import com.example.shikiflow.utils.Converter.toAbbreviation

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun AnimeTranslationSelectScreen(
    title: String,
    shikimoriId: Int,
    navOptions: AnimeWatchNavOptions,
    onNavigateBack: () -> Unit,
    animeTranslationsViewModel: AnimeTranslationsViewModel = hiltViewModel()
) {
    val filters = TranslationFilter.entries
    var translationFilter by rememberSaveable { mutableStateOf(TranslationFilter.ALL) }
    var isNavigating by remember { mutableStateOf(false) }
    val uiState by animeTranslationsViewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(shikimoriId) {
        animeTranslationsViewModel.setId(shikimoriId)
    }

    val lazyListState = rememberLazyListState()
    val isAtTop by remember {
        derivedStateOf {
            if (isNavigating) { true } else {
                lazyListState.firstVisibleItemIndex == 0 &&
                lazyListState.firstVisibleItemScrollOffset == 0
            }
        }
    }
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
        snapAnimationSpec = MaterialTheme.motionScheme.defaultSpatialSpec()
    )

    LaunchedEffect(translationFilter) {
        isNavigating = true
        lazyListState.animateScrollToItem(0)
        isNavigating = false
    }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
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
                        IconButton(onClick = onNavigateBack) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back"
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = if(isAtTop) MaterialTheme.colorScheme.background
                            else MaterialTheme.colorScheme.surfaceContainer
                    ),
                    windowInsets = WindowInsets.statusBars.only(WindowInsetsSides.Top)
                )
                TopAppBar(
                    windowInsets = WindowInsets(),
                    expandedHeight = 48.dp,
                    title = {
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            contentPadding = PaddingValues(horizontal = 16.dp)
                        ) {
                            items(filters.size) { index ->
                                val filter = filters[index]

                                FilterChip(
                                    selected = translationFilter == filter,
                                    leadingIcon = {
                                        if(translationFilter == filter) {
                                            Icon(
                                                imageVector = Icons.Default.Check,
                                                contentDescription = null,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                    },
                                    label = {
                                        Text(
                                            text = stringResource(id = filter.displayValue),
                                            style = MaterialTheme.typography.labelMedium,
                                            modifier = Modifier.padding(vertical = 8.dp)
                                        )
                                    },
                                    onClick = {
                                        if (filter != translationFilter)
                                            translationFilter = filter
                                    }
                                )
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.background,
                        scrolledContainerColor = MaterialTheme.colorScheme.background
                    ),
                    scrollBehavior = scrollBehavior
                )
                HorizontalDivider()
            }
        }
    ) { paddingValues ->
        PullToRefreshCustomBox(
            isRefreshing = uiState.isRefreshing,
            enabled = scrollBehavior.state.collapsedFraction == 0f,
            onRefresh = { animeTranslationsViewModel.onRefresh() },
            modifier = Modifier
                .fillMaxSize()
                .padding(top = paddingValues.calculateTopPadding())
        ) {
            LazyColumn(
                state = lazyListState,
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(
                    start = 12.dp,
                    end = 12.dp,
                    top = 8.dp,
                    bottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
                ),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if(uiState.isLoading) {
                    item {
                        Box(
                            modifier = Modifier.fillParentMaxSize(),
                            contentAlignment = Alignment.Center
                        ) { CircularProgressIndicator() }
                    }
                } else if(uiState.errorMessage != null) {
                    item {
                        Box(
                            modifier = Modifier.fillParentMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            ErrorItem(
                                message = uiState.errorMessage ?: stringResource(R.string.common_error),
                                buttonLabel = stringResource(R.string.common_retry),
                                onButtonClick = { animeTranslationsViewModel.onRefresh() }
                            )
                        }
                    }
                } else {
                    uiState.translations.let { animeTranslations ->
                        val translations = animeTranslations[translationFilter] ?: emptyList()

                        if(translations.isNotEmpty()) {
                            items(
                                count = translations.size,
                                key = { index -> translations[index].id }
                            ) { index ->
                                val translation = translations[index]

                                AnimeTranslationItem(
                                    kodikAnime = translation,
                                    onTranslationClick = { link, translationGroup, episodesRange ->
                                        if(episodesRange.last == 0) {
                                            navOptions.navigateToEpisodeScreen(
                                                playerNavigate = EpisodeMetadata(
                                                    link = link,
                                                    translationGroup = translationGroup,
                                                    episodeNum = 0,
                                                    firstEpisode = episodesRange.first,
                                                    lastEpisode = 0
                                                )
                                            )
                                        } else {
                                            navOptions.navigateToEpisodeSelection(link, translationGroup, episodesRange)
                                        }
                                    },
                                    modifier = Modifier.animateItem()
                                )
                            }
                        } else {
                            item {
                                Box(
                                    modifier = Modifier.fillParentMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    ErrorItem(
                                        message = stringResource(R.string.translation_empty_response)
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
private fun AnimeTranslationItem(
    kodikAnime: KodikAnime,
    onTranslationClick: (String, String, IntRange) -> Unit,
    modifier: Modifier = Modifier
) {
    val episodesRange = if(kodikAnime.episodesCount != null && kodikAnime.lastEpisode != null) {
        (kodikAnime.lastEpisode - kodikAnime.episodesCount + 1)..(kodikAnime.lastEpisode)
    } else 0..0

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable {
                onTranslationClick(
                    kodikAnime.link,
                    kodikAnime.translation.title,
                    episodesRange
                )
            }
            .padding(horizontal = 4.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.Start),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.secondary.copy(alpha = 0.5f)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = kodikAnime.translation.title.toAbbreviation(),
                style = MaterialTheme.typography.titleMedium.copy(
                    color = MaterialTheme.colorScheme.primary
                )
            )
        }
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp, Alignment.Top)
        ) {
            Text(
                text = kodikAnime.translation.title,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = stringResource(
                    R.string.translation_updated_at,
                    Converter.formatInstant(kodikAnime.updatedAt, includeTime = true)
                ),
                style = MaterialTheme.typography.labelSmall
            )
        }
        Text(
            text = stringResource(R.string.episodes, kodikAnime.episodesCount ?: 1),
            style = MaterialTheme.typography.labelMedium
        )
    }
}