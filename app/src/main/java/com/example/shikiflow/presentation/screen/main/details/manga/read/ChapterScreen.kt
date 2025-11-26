package com.example.shikiflow.presentation.screen.main.details.manga.read

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.shikiflow.R
import com.example.shikiflow.presentation.common.ErrorItem
import com.example.shikiflow.presentation.common.image.ChapterItem
import com.example.shikiflow.presentation.viewmodel.manga.read.ChapterNavigationViewModel
import com.example.shikiflow.presentation.viewmodel.manga.read.ChapterViewModel
import com.example.shikiflow.utils.Resource
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import net.engawapg.lib.zoomable.rememberZoomState
import net.engawapg.lib.zoomable.zoomable

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ChapterScreen(
    mangaDexChapterId: String,
    chapterNumber: String,
    title: String?,
    navOptions: MangaReadNavOptions,
    chapterViewModel: ChapterViewModel = hiltViewModel()
) {
    val chapterSettings by chapterViewModel.mangaSettings.collectAsStateWithLifecycle()
    val chapterContent by chapterViewModel.chapterContent.collectAsStateWithLifecycle()

    val showBottomSheet = remember { mutableStateOf(false) }
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(
        snapAnimationSpec = MaterialTheme.motionScheme.defaultSpatialSpec()
    )
    var chapterPage by remember { mutableIntStateOf(1) }

    LaunchedEffect(mangaDexChapterId) {
        chapterViewModel.downloadMangaChapter(mangaDexChapterId)
    }

    Scaffold(
        modifier = Modifier.then(
            other = if(chapterSettings.chapterUIMode == ChapterUIMode.SCROLL) {
                Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
            } else Modifier
        ),
        topBar = {
            Column {
                TopAppBar(
                    title = {
                        Text(
                            text = if(!title.isNullOrEmpty()) {
                                stringResource(id = R.string.chapter_title, chapterNumber, title)
                            } else {
                                stringResource(id = R.string.chapter_empty_title, chapterNumber)
                            },
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            style = MaterialTheme.typography.titleMedium
                        )
                    },
                    scrollBehavior = scrollBehavior,
                    navigationIcon = {
                        IconButton(
                            onClick = { navOptions.navigateBack() }
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back to Main"
                            )
                        }
                    },
                    actions = {
                        IconButton(
                            onClick = { showBottomSheet.value = true }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = "Chapter UI Settings",
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                )
                HorizontalDivider()
            }
        }
    ) { paddingValues ->
        when(chapterContent) {
            is Resource.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) { CircularProgressIndicator() }
            }
            is Resource.Success -> {
                chapterContent.data?.let { pageUrls ->
                    when(chapterSettings.chapterUIMode) {
                        ChapterUIMode.PAGE -> {
                            ChapterPageModeComponent(
                                chapterPageUrls = pageUrls,
                                chapterPage = chapterPage,
                                onPageChange = { pageNumber -> chapterPage = pageNumber },
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(
                                        top = paddingValues.calculateTopPadding(),
                                        start = paddingValues.calculateStartPadding(LayoutDirection.Ltr),
                                        end = paddingValues.calculateEndPadding(LayoutDirection.Ltr)
                                    )
                            )
                        }
                        ChapterUIMode.SCROLL -> {
                            ChapterScrollModeComponent(
                                chapterPageUrls = pageUrls,
                                initialPage = chapterPage,
                                onPageChange = { pageNumber -> chapterPage = pageNumber },
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(
                                        top = paddingValues.calculateTopPadding(),
                                        start = paddingValues.calculateStartPadding(LayoutDirection.Ltr),
                                        end = paddingValues.calculateEndPadding(LayoutDirection.Ltr),
                                    )
                            )
                        }
                    }
                }
            }
            is Resource.Error -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    ErrorItem(
                        message = chapterContent.message ?: stringResource(R.string.common_error),
                        buttonLabel = stringResource(R.string.common_retry),
                        onButtonClick = { chapterViewModel.downloadMangaChapter(mangaDexChapterId, true) }
                    )
                }
            }
        }
        if(showBottomSheet.value) {
            ChapterSettingsBottomSheet(
                mangaSettings = chapterSettings,
                onDismiss = { showBottomSheet.value = false },
                onSettingsChange = { newSettings ->
                    chapterViewModel.updateSettings(newSettings)
                }
            )
        }
    }
}

@Composable
private fun ChapterScrollModeComponent(
    chapterPageUrls: List<String>,
    initialPage: Int,
    onPageChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
    chapterNavigationViewModel: ChapterNavigationViewModel = hiltViewModel()
) {
    val isNavigationVisible by chapterNavigationViewModel.isNavigationVisible.collectAsStateWithLifecycle()
    val lazyListState = rememberLazyListState(
        initialFirstVisibleItemIndex = initialPage - 1
    )
    val scope = rememberCoroutineScope()

    val currentPage by remember {
        derivedStateOf {
            lazyListState.firstVisibleItemIndex + 1
        }
    }

    LaunchedEffect(currentPage) {
        onPageChange(currentPage)
    }

    LaunchedEffect(lazyListState) {
        snapshotFlow { lazyListState.firstVisibleItemScrollOffset }
            .distinctUntilChanged()
            .collect {
                chapterNavigationViewModel.onScrollDetected()
            }
    }

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomCenter
    ) {
        LazyColumn(
            state = lazyListState,
            modifier = Modifier
                .fillMaxSize()
                .zoomable(rememberZoomState()), //Well, it's easier this way I guess
            verticalArrangement = Arrangement.spacedBy(4.dp),
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            items(chapterPageUrls.size) { index ->
                val chapterPageUrl = chapterPageUrls[index]

                ChapterItem(
                    pageUrl = chapterPageUrl,
                    pageNumber = index + 1
                )
            }
        }
        AnimatedVisibility(
            visible = isNavigationVisible,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            ChapterNavigationComponent(
                modifier = Modifier
                    .navigationBarsPadding()
                    .imePadding(),
                currentPage = currentPage,
                pageCount = chapterPageUrls.size,
                onNavigateClick = { pageNumber ->
                    scope.launch {
                        lazyListState.animateScrollToItem(pageNumber - 1)
                    }
                },
                onInteractionStart = chapterNavigationViewModel::onUserInteractionStart,
                onInteractionEnd = chapterNavigationViewModel::onUserInteractionEnd
            )
        }
    }
}