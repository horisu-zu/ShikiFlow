package com.example.shikiflow.presentation.screen.main.details.manga.read

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.List
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.shikiflow.R
import com.example.shikiflow.domain.model.mangadex.chapter_metadata.MangaChapterMetadata
import com.example.shikiflow.domain.model.settings.ChapterUIMode
import com.example.shikiflow.presentation.common.ErrorItem
import com.example.shikiflow.presentation.common.SideSheet
import com.example.shikiflow.presentation.viewmodel.manga.read.ChapterViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ChapterScreen(
    chapterUiData: ChapterUiData,
    navOptions: MangaReadNavOptions,
    chapterViewModel: ChapterViewModel = hiltViewModel()
) {
    val chapterUiState by chapterViewModel.chapterUiState.collectAsStateWithLifecycle()
    val chaptersList = chapterViewModel.getMangaChapters(
        mangaId = chapterUiData.mangaId,
        groupIds = chapterUiData.scanlationGroupIds,
        uploader = chapterUiData.uploader
    ).collectAsLazyPagingItems()

    val showBottomSheet = remember { mutableStateOf(false) }
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(
        snapAnimationSpec = MaterialTheme.motionScheme.defaultSpatialSpec()
    )
    var chapterPage by remember { mutableIntStateOf(1) }
    var isSheetOpen by remember { mutableStateOf(false) }

    LaunchedEffect(chapterUiState.uiSettings.isDataSaverEnabled) {
        chapterViewModel.loadChapter(
            mangaDexChapterId = chapterUiData.chapterId,
            isDataSaver = chapterUiState.uiSettings.isDataSaverEnabled
        )
    }

    SideSheet(
        isSheetOpen = isSheetOpen,
        onDismiss = { isSheetOpen = false },
        sheetContent = {
            item {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.background)
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(R.string.chapter_list_label),
                        style = MaterialTheme.typography.titleMedium.copy(
                            textAlign = TextAlign.Center
                        )
                    )
                }
            }
            if(chaptersList.loadState.refresh is LoadState.Loading) {
                item {
                    Box(
                        modifier = Modifier.fillParentMaxSize(),
                        contentAlignment = Alignment.Center
                    ) { CircularProgressIndicator() }
                }
            } else if(chaptersList.loadState.refresh is LoadState.Error) {
                item {
                    Box(
                        modifier = Modifier.fillParentMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        ErrorItem(
                            message = stringResource(R.string.common_error),
                            buttonLabel = stringResource(id = R.string.common_retry),
                            onButtonClick = { chaptersList.refresh() }
                        )
                    }
                }
            } else {
                items(chaptersList.itemCount) { index ->
                    chaptersList[index]?.let { chapterItem ->
                        ChaptersListItem(
                            chapterMetadata = chapterItem,
                            onItemClick = { id, chapterNum, title ->
                                navOptions.navigateToChapter(
                                    chapterUiData = chapterUiData.copy(
                                        chapterId = id,
                                        chapterNumber = chapterNum,
                                        title = title
                                    )
                                )
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        },
        mainContent = {
            Scaffold(
                modifier = Modifier.then(
                    other = if(chapterUiState.uiSettings.chapterUIMode == ChapterUIMode.SCROLL) {
                        Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
                    } else Modifier
                ),
                topBar = {
                    Column {
                        TopAppBar(
                            title = {
                                Text(
                                    text = buildString {
                                        chapterUiData.chapterNumber?.let { chapterNumber ->
                                            append(
                                                stringResource(id = R.string.media_item_chapter, chapterNumber)
                                            )
                                        }
                                        if(!chapterUiData.title.isNullOrEmpty()) {
                                            append(" — ${chapterUiData.title}")
                                        }
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
                                IconButton(
                                    onClick = { isSheetOpen = true }
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.List,
                                        contentDescription = "Scanlation Group Chapters",
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }
                        )
                        HorizontalDivider()
                    }
                }
            ) { paddingValues ->
                if(chapterUiState.isLoading) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                } else if(chapterUiState.chapterError != null) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        ErrorItem(
                            message = chapterUiState.chapterError ?: stringResource(R.string.common_error),
                            buttonLabel = stringResource(R.string.common_retry),
                            onButtonClick = {
                                chapterViewModel.loadChapter(
                                    mangaDexChapterId = chapterUiData.chapterId,
                                    isDataSaver = chapterUiState.uiSettings.isDataSaverEnabled
                                )
                            }
                        )
                    }
                } else {
                    Box(modifier = Modifier.fillMaxSize()) {
                        when(chapterUiState.uiSettings.chapterUIMode) {
                            ChapterUIMode.PAGE -> {
                                ChapterPageModeComponent(
                                    chapterPageUrls = chapterUiState.chapterData,
                                    chapterPage = chapterPage,
                                    onPageChange = { pageNumber -> chapterPage = pageNumber },
                                    onScrollDetected = chapterViewModel::onInteractionStart,
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(
                                            top = paddingValues.calculateTopPadding()
                                        )
                                )
                            }
                            ChapterUIMode.SCROLL -> {
                                ChapterScrollModeComponent(
                                    chapterPageUrls = chapterUiState.chapterData,
                                    chapterPage = chapterPage,
                                    onPageChange = { pageNumber -> chapterPage = pageNumber },
                                    onScrollDetected = chapterViewModel::onInteractionStart,
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(top = paddingValues.calculateTopPadding())
                                )
                            }
                        }
                        AnimatedContent(
                            targetState = chapterUiState.isNavigationVisible,
                            transitionSpec = {
                                fadeIn() togetherWith fadeOut() using SizeTransform(clip = false)
                            },
                            contentAlignment = Alignment.BottomCenter,
                            modifier = Modifier.align(Alignment.BottomCenter)
                        ) { isVisible ->
                            when(isVisible) {
                                true -> {
                                    ChapterNavigationComponent(
                                        currentPage = chapterPage,
                                        pageCount = chapterUiState.chapterData.size,
                                        onNavigateClick = { pageNumber -> chapterPage = pageNumber },
                                        onFocusChange = { isFocused ->
                                            chapterViewModel.changeFocusedState(isFocused)
                                        },
                                        modifier = Modifier
                                            .navigationBarsPadding()
                                            .imePadding()
                                            .padding(bottom = 4.dp)
                                    )
                                }
                                false -> {
                                    if(chapterUiState.uiSettings.chapterUIMode == ChapterUIMode.PAGE) {
                                        ChapterProgressBarComponent(
                                            currentPage = chapterPage,
                                            pageCount = chapterUiState.chapterData.size,
                                            onSegmentClick = { pageNumber -> chapterPage = pageNumber },
                                            modifier = Modifier.navigationBarsPadding()
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
                if(showBottomSheet.value) {
                    ChapterSettingsBottomSheet(
                        mangaSettings = chapterUiState.uiSettings,
                        onDismiss = { showBottomSheet.value = false },
                        onSettingsChange = { newSettings ->
                            chapterViewModel.updateSettings(newSettings)
                        }
                    )
                }
            }
        }
    )
}

@Composable
private fun ChaptersListItem(
    chapterMetadata: MangaChapterMetadata,
    onItemClick: (String, String?, String?) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable {
                onItemClick(
                    chapterMetadata.id,
                    chapterMetadata.attributes.chapter,
                    chapterMetadata.attributes.title
                )
            }
            .padding(horizontal = 6.dp, vertical = 4.dp)
    ) {
        Text(
            text = buildString {
                chapterMetadata.attributes.chapter?.let { chapterNumber ->
                    append(stringResource(R.string.media_item_chapter, chapterNumber))
                }
                chapterMetadata.attributes.title?.let { chapterTitle ->
                    append(" — $chapterTitle")
                }
            },
            style = MaterialTheme.typography.bodySmall
        )
    }
}