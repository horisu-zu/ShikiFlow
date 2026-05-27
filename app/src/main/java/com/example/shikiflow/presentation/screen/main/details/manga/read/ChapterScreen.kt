package com.example.shikiflow.presentation.screen.main.details.manga.read

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.VisibilityThreshold
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
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
import com.example.shikiflow.presentation.common.systemBarsVisibility
import com.example.shikiflow.presentation.viewmodel.manga.read.chapter.ChapterViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ChapterScreen(
    malId: Int?,
    trackerMangaId: Int,
    chapterUiData: ChapterUiData,
    navOptions: MangaReadNavOptions,
    chapterViewModel: ChapterViewModel = hiltViewModel()
) {
    val chapterUiState by chapterViewModel.chapterUiState.collectAsStateWithLifecycle()
    val chaptersList = chapterViewModel.mangaChaptersItems.collectAsLazyPagingItems()

    val showBottomSheet = remember { mutableStateOf(false) }
    var isSheetOpen by remember { mutableStateOf(false) }

    LaunchedEffect(chapterUiState.uiSettings.isDataSaverEnabled) {
        chapterViewModel.setChapterData(
            mangaId = chapterUiData.mangaId,
            trackerMangaId = trackerMangaId,
            malId = malId,
            chapterId = chapterUiData.chapterId,
            chapterNumber = chapterUiData.chapterNumber?.toDoubleOrNull() ?: 0.0,
            scanlationGroupsIds = chapterUiData.scanlationGroupIds,
            uploader = chapterUiData.uploader
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
            when (chaptersList.loadState.refresh) {
                is LoadState.Loading -> {
                    item {
                        Box(
                            modifier = Modifier.fillParentMaxSize(),
                            contentAlignment = Alignment.Center
                        ) { CircularProgressIndicator() }
                    }
                }
                is LoadState.Error -> {
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
                }
                else -> {
                    items(chaptersList.itemCount) { index ->
                        chaptersList[index]?.let { chapterItem ->
                            ChaptersListItem(
                                chapterMetadata = chapterItem,
                                isCurrent = chapterItem.attributes.chapter == chapterUiData.chapterNumber,
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
                    chaptersList.apply {
                        when {
                            loadState.append is LoadState.Error -> {
                                item {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 8.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        ErrorItem(
                                            message = stringResource(R.string.common_error),
                                            buttonLabel = stringResource(R.string.common_retry),
                                            onButtonClick = { chaptersList.retry() }
                                        )
                                    }
                                }
                            }
                            loadState.append is LoadState.Loading -> {
                                item {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 8.dp),
                                        contentAlignment = Alignment.Center
                                    ) { CircularProgressIndicator() }
                                }
                            }
                        }
                    }
                }
            }
        },
        mainContent = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .systemBarsVisibility(chapterUiState.isNavigationVisible),
                contentAlignment = Alignment.Center
            ) {
                if(chapterUiState.isLoading) {
                    CircularProgressIndicator()
                } else if(chapterUiState.chapterError != null) {
                    ErrorItem(
                        message = chapterUiState.chapterError ?: stringResource(R.string.common_error),
                        buttonLabel = stringResource(R.string.common_retry),
                        onButtonClick = { chapterViewModel.onRefresh() }
                    )
                } else {
                    when (chapterUiState.uiSettings.chapterUIMode) {
                        ChapterUIMode.PAGE -> {
                            ChapterPageModeComponent(
                                chapterPageUrls = chapterUiState.chapterData,
                                chapterPageIndex = chapterUiState.currentPageIndex,
                                onPageChange = { pageIndex ->
                                    chapterViewModel.updatePage(pageIndex)
                                },
                                onNavigationChange = chapterViewModel::onNavigationChange,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                        ChapterUIMode.SCROLL -> {
                            ChapterScrollModeComponent(
                                chapterPageUrls = chapterUiState.chapterData,
                                chapterPageIndex = chapterUiState.currentPageIndex,
                                onPageChange = { pageIndex ->
                                    chapterViewModel.updatePage(pageIndex)
                                },
                                onNavigationChange = chapterViewModel::onNavigationChange,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }

                    AnimatedVisibility(
                        visible = chapterUiState.isNavigationVisible,
                        enter = fadeIn() + slideInVertically(
                            animationSpec = spring(
                                stiffness = Spring.StiffnessMedium,
                                visibilityThreshold = IntOffset.VisibilityThreshold
                            ),
                            initialOffsetY = { offset -> -offset / 2 }
                        ),
                        exit = fadeOut() + slideOutVertically(
                            animationSpec = spring(
                                stiffness = Spring.StiffnessMedium,
                                visibilityThreshold = IntOffset.VisibilityThreshold
                            ),
                            targetOffsetY = { offset -> -offset / 2 }
                        ),
                        modifier = Modifier.align(Alignment.TopCenter)
                    ) {
                        TopAppBar(
                            title = {
                                Text(
                                    text = buildString {
                                        chapterUiData.chapterNumber?.let { chapterNumber ->
                                            append(
                                                stringResource(id = R.string.chapter_short_title, chapterNumber)
                                            )
                                        }
                                        if(!chapterUiData.title.isNullOrEmpty()) {
                                            append(" - ")
                                            append(chapterUiData.title)
                                        }
                                    },
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    style = MaterialTheme.typography.titleSmall
                                )
                            },
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
                    }

                    AnimatedContent(
                        targetState = chapterUiState.isNavigationVisible,
                        transitionSpec = {
                            fadeIn() togetherWith fadeOut() using SizeTransform(clip = false)
                        },
                        contentAlignment = Alignment.BottomCenter,
                        modifier = Modifier.align(Alignment.BottomCenter)
                    ) { isVisible ->
                        when (isVisible) {
                            true -> {
                                ChapterNavigationComponent(
                                    currentPage = chapterUiState.currentPageIndex + 1,
                                    pageCount = chapterUiState.chapterData.size,
                                    onNavigateClick = { pageNumber ->
                                        chapterViewModel.updatePage(pageNumber - 1)
                                    },
                                    onSheetOpenClick = { isSheetOpen = true },
                                    modifier = Modifier
                                        .navigationBarsPadding()
                                        .imePadding()
                                        .padding(bottom = 4.dp)
                                )
                            }
                            false -> {
                                if (chapterUiState.uiSettings.chapterUIMode == ChapterUIMode.PAGE) {
                                    ChapterProgressBarComponent(
                                        currentPage = chapterUiState.currentPageIndex + 1,
                                        pageCount = chapterUiState.chapterData.size,
                                        onSegmentClick = { pageNumber ->
                                            chapterViewModel.updatePage(pageNumber - 1)
                                        },
                                        modifier = Modifier.padding(bottom = 12.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            if(showBottomSheet.value) {
                ReaderSettingsBottomSheet(
                    mangaSettings = chapterUiState.uiSettings,
                    onDismiss = { showBottomSheet.value = false },
                    onSettingsChange = { newSettings ->
                        chapterViewModel.updateSettings(newSettings)
                    }
                )
            }
        }
    )
}

@Composable
private fun ChaptersListItem(
    chapterMetadata: MangaChapterMetadata,
    isCurrent: Boolean,
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
            .background(
                color = if(isCurrent) MaterialTheme.colorScheme.background
                    else Color.Unspecified
            )
            .padding(horizontal = 6.dp, vertical = 4.dp)
    ) {
        Text(
            text = buildString {
                chapterMetadata.attributes.chapter?.let { chapterNumber ->
                    append(stringResource(R.string.media_item_chapter, chapterNumber))
                }
                chapterMetadata.attributes.title?.let { chapterTitle ->
                    append(" - $chapterTitle")
                }
            },
            style = MaterialTheme.typography.bodyMedium
        )
    }
}