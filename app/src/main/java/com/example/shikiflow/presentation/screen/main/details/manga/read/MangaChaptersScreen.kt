package com.example.shikiflow.presentation.screen.main.details.manga.read

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.shikiflow.presentation.viewmodel.manga.read.MangaChaptersViewModel
import com.example.shikiflow.utils.Resource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MangaChaptersScreen(
    mangaDexId: String,
    title: String,
    completedChapters: Int,
    navOptions: MangaReadNavOptions,
    onNavigateBack: () -> Unit,
    navigationSource: ChaptersScreenSource,
    mangaChaptersViewModel: MangaChaptersViewModel = hiltViewModel()
) {
    val mangaChapters = mangaChaptersViewModel.mangaChapters.collectAsState()

    val lazyListState = rememberLazyListState()
    val isAtTop by remember {
        derivedStateOf {
            lazyListState.firstVisibleItemIndex == 0 && lazyListState.firstVisibleItemScrollOffset == 0
        }
    }

    LaunchedEffect(mangaDexId) {
        mangaChaptersViewModel.getMangaChapters(mangaDexId)
    }

    Scaffold(
        topBar = {
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
                        onClick = { when(navigationSource) {
                            ChaptersScreenSource.AUTOMATED -> onNavigateBack()
                            ChaptersScreenSource.MANUAL -> navOptions.navigateBack()
                        } }
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back to Main"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = if(isAtTop) MaterialTheme.colorScheme.background
                        else MaterialTheme.colorScheme.surfaceVariant
                )
            )
        }, modifier = Modifier.fillMaxSize()
    ) { paddingValues ->
        when(mangaChapters.value) {
            is Resource.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) { CircularProgressIndicator() }
            }
            is Resource.Success -> {
                val chapters = mangaChapters.value.data
                val sortedChapters = chapters?.keys
                    ?.sortedBy { it.toFloat() } ?: emptyList()

                LazyColumn(
                    state = lazyListState,
                    modifier = Modifier.fillMaxSize().padding(
                        top = paddingValues.calculateTopPadding(),
                        start = paddingValues.calculateStartPadding(LayoutDirection.Ltr),
                        end = paddingValues.calculateEndPadding(LayoutDirection.Ltr),
                    ), contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    val startsFromZero = sortedChapters.firstOrNull()?.toFloat() == 0f

                    items(sortedChapters){ chapterNumber ->
                        ChapterItem(
                            chapterNumber = chapterNumber,
                            onChapterClick = {
                                val chapterIds = chapters?.get(chapterNumber) ?: emptyList()
                                Log.d("ChapterClick", "Chapter: $chapterNumber, IDs count: ${chapterIds.size}, IDs: $chapterIds")

                                navOptions.navigateToChapterTranslations(
                                    chapterTranslationIds = chapterIds,
                                    chapterNumber = chapterNumber
                                )
                            },
                            isCompleted = if(startsFromZero) {
                                (chapterNumber.toFloat()) < completedChapters
                            } else (chapterNumber.toFloat()) <= completedChapters
                        )
                    }
                }
            }
            is Resource.Error -> { /*TODO*/ }
        }
    }
}

@Composable
private fun ChapterItem(
    chapterNumber: String,
    onChapterClick: (String) -> Unit,
    isCompleted: Boolean,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp))
            .clickable { onChapterClick(chapterNumber) }
            .padding(horizontal = 6.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        if(isCompleted) {
            Icon(
                imageVector = Icons.Default.Check,
                tint = MaterialTheme.colorScheme.onSurface,
                contentDescription = "Completed Chapter",
                modifier = Modifier.size(24.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.75f))
                    .padding(4.dp)
            )
        }
        Text(
            text = "Chapter $chapterNumber",
            style = MaterialTheme.typography.bodyLarge.copy(
                fontWeight = FontWeight.SemiBold
            )
        )
    }
}
