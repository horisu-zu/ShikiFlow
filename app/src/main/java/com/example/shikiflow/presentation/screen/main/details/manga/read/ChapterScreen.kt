package com.example.shikiflow.presentation.screen.main.details.manga.read

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChapterScreen(
    mangaDexChapterId: String,
    chapterNumber: String,
    title: String?,
    navOptions: MangaReadNavOptions,
    chapterViewModel: ChapterViewModel = hiltViewModel()
) {
    val chapterUiMode = chapterViewModel.chapterUiMode.collectAsState()
    val chapterPages = chapterViewModel.chapterPages.collectAsState()

    val showBottomSheet = remember { mutableStateOf(false) }
    val topAppBarState = rememberTopAppBarState()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(
        state = topAppBarState,
        snapAnimationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessVeryLow
        )
    )

    LaunchedEffect(mangaDexChapterId) {
        chapterViewModel.downloadMangaChapter(mangaDexChapterId)
    }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = {
                    Text(text = buildAnnotatedString {
                        append("Ch. $chapterNumber")
                        if(!title.isNullOrEmpty()) { append(" - $title") }
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
                            Icons.AutoMirrored.Filled.ArrowBack,
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
        }
    ) { paddingValues ->
        when(val pageUrls = chapterPages.value) {
            is Resource.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) { CircularProgressIndicator() }
            }
            is Resource.Success -> {
                if(pageUrls.data.isNullOrEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize().padding(
                            top = paddingValues.calculateTopPadding(),
                            start = paddingValues.calculateStartPadding(LayoutDirection.Ltr),
                            end = paddingValues.calculateEndPadding(LayoutDirection.Ltr),
                        ), contentAlignment = Alignment.Center
                    ) {
                        ErrorItem(
                            message = stringResource(R.string.chp_mangadex_empty),
                            buttonLabel = stringResource(R.string.chp_navigate_back),
                            onButtonClick = { navOptions.navigateBack() }
                        )
                    }
                } else {
                    when(chapterUiMode.value) {
                        ChapterUIMode.PAGE -> {
                            ChapterPageModeComponent(
                                chapterPageUrls = pageUrls.data,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(
                                        top = paddingValues.calculateTopPadding(),
                                        start = paddingValues.calculateStartPadding(LayoutDirection.Ltr),
                                        end = paddingValues.calculateEndPadding(LayoutDirection.Ltr),
                                    )
                            )
                        }
                        ChapterUIMode.SCROLL -> {
                            ChapterScrollModeComponent(
                                chapterPageUrls = pageUrls.data,
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
                    if(showBottomSheet.value) {
                        ChapterSettingsBottomSheet(
                            onDismiss = { showBottomSheet.value = false }
                        )
                    }
                }
            }
            is Resource.Error -> { /**/ }
        }
    }
}

@Composable
private fun ChapterScrollModeComponent(
    chapterPageUrls: List<String>,
    modifier: Modifier = Modifier,
    chapterNavigationViewModel: ChapterNavigationViewModel = hiltViewModel()
) {
    var scale by remember { mutableFloatStateOf(1f) }
    var offsetX by remember { mutableFloatStateOf(0f) }
    var offsetY by remember { mutableFloatStateOf(0f) }

    val isNavigationVisible = chapterNavigationViewModel.isNavigationVisible.collectAsState()
    val lazyListState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    val currentPage by remember {
        derivedStateOf {
            lazyListState.firstVisibleItemIndex + 1
        }
    }

    LaunchedEffect(lazyListState) {
        snapshotFlow { lazyListState.firstVisibleItemIndex }
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
                .graphicsLayer(
                    scaleX = scale,
                    scaleY = scale,
                    translationX = offsetX,
                    translationY = offsetY
                )
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
            visible = isNavigationVisible.value,
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
                        lazyListState.scrollToItem(pageNumber - 1)
                    }
                },
                onInteractionStart = chapterNavigationViewModel::onUserInteractionStart,
                onInteractionEnd = chapterNavigationViewModel::onUserInteractionEnd
            )
        }
    }
}