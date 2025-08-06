package com.example.shikiflow.presentation.screen.main.details.manga.read

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil3.compose.SubcomposeAsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.example.shikiflow.presentation.common.image.shimmerEffect
import com.example.shikiflow.presentation.viewmodel.manga.read.ChapterViewModel
import com.example.shikiflow.utils.Resource
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun ChapterScreen(
    mangaDexChapterId: String,
    chapterViewModel: ChapterViewModel = hiltViewModel()
) {
    val chapterUiMode = chapterViewModel.chapterUiMode.collectAsState()
    val chapterPages = chapterViewModel.chapterPages.collectAsState()

    LaunchedEffect(mangaDexChapterId) {
        chapterViewModel.downloadMangaChapter(mangaDexChapterId)
    }

    Scaffold { paddingValues ->
        when(val pageUrls = chapterPages.value) {
            is Resource.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) { CircularProgressIndicator() }
            }
            is Resource.Success -> {
                when(chapterUiMode.value) {
                    ChapterUIMode.PAGE -> { /**/ }
                    ChapterUIMode.SCROLL -> {
                        ChapterScrollComponent(
                            chapterPageUrls = pageUrls.data ?: emptyList(),
                            modifier = Modifier.fillMaxSize().padding(
                                top = paddingValues.calculateTopPadding(),
                                start = paddingValues.calculateStartPadding(LayoutDirection.Ltr),
                                end = paddingValues.calculateEndPadding(LayoutDirection.Ltr),
                            )
                        )
                    }
                }
            }
            is Resource.Error -> { /**/ }
        }
    }
}

@Composable
private fun ChapterScrollComponent(
    chapterPageUrls: List<String>,
    modifier: Modifier = Modifier
) {
    val lazyListState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    val isNavigationVisible = remember { mutableStateOf(false) }
    val isUserInteracting = remember { mutableStateOf(false) }
    val currentPage by remember {
        derivedStateOf {
            lazyListState.firstVisibleItemIndex + 1
        }
    }

    LaunchedEffect(lazyListState.isScrollInProgress, isUserInteracting.value) {
        if (!isUserInteracting.value) {
            isNavigationVisible.value = true
            delay(2000)
            if (!isUserInteracting.value) {
                isNavigationVisible.value = false
            }
        }
    }

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomCenter
    ) {
        LazyColumn(
            state = lazyListState,
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(4.dp)
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
                modifier = Modifier.imePadding(),
                currentPage = currentPage,
                pageCount = chapterPageUrls.size,
                onNavigateClick = { pageNumber ->
                    scope.launch {
                        lazyListState.scrollToItem(pageNumber - 1)
                    }
                },
                onInteractionStart = { isUserInteracting.value = true },
                onInteractionEnd = { isUserInteracting.value = false }
            )
        }
    }
}

@Composable
private fun ChapterNavigationComponent(
    currentPage: Int,
    pageCount: Int,
    onNavigateClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
    onInteractionStart: () -> Unit = {},
    onInteractionEnd: () -> Unit = {}
) {
    var pageInput by remember { mutableStateOf(currentPage.toString()) }
    val focusManager = LocalFocusManager.current
    val imeBottom = WindowInsets.ime.getBottom(LocalDensity.current)

    LaunchedEffect(imeBottom) {
        if (imeBottom == 0) {
            focusManager.clearFocus()
        }
    }

    Row(
        modifier = modifier.wrapContentWidth().clip(RoundedCornerShape(16.dp))
            .background(Color.Black.copy(alpha = 0.45f)),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        IconButton(
            onClick = { onNavigateClick(currentPage - 1) },
            enabled = currentPage > 1,
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                contentDescription = "Previous Page",
                modifier = Modifier.size(24.dp)
            )
        }
        BasicTextField(
            value = pageInput,
            onValueChange = { newValue ->
                pageInput = newValue.filter { it.isDigit() }
            },
            textStyle = MaterialTheme.typography.bodyMedium.copy(
                color = Color.White
            ),
            modifier = Modifier.width(32.dp).onFocusChanged {
                if(it.isFocused) {
                    onInteractionStart()
                } else onInteractionEnd()
            },
        )
        Text(
            text = "/ $pageCount",
            style = MaterialTheme.typography.bodyMedium
        )
        IconButton(
            onClick = { onNavigateClick(currentPage + 1) },
            enabled = currentPage < pageCount,
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = "Next Page",
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
private fun ChapterItem(
    pageUrl: String,
    pageNumber: Int,
    modifier: Modifier = Modifier
) {
    SubcomposeAsyncImage(
        model = ImageRequest.Builder(LocalContext.current)
            .data(pageUrl)
            .memoryCacheKey(pageUrl)
            //.diskCacheKey(pageUrl)
            .listener(
                onSuccess = { _, result ->
                    Log.d("Image", "Image successfully loaded: $result")
                },
                onError = { _, error ->
                    Log.d("Image", "Error loading image: $error")
                }
            )
            .crossfade(true)
            .build(),
        contentDescription = pageUrl,
        contentScale = ContentScale.Crop,
        modifier = modifier.fillMaxWidth(),
        loading = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .shimmerEffect(),
                contentAlignment = Alignment.Center
            ) { Text(
                text = pageNumber.toString(),
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.25f)
                )
            ) }
        }
    )
}