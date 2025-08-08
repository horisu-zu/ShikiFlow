package com.example.shikiflow.presentation.screen.main.details.manga.read

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import com.example.shikiflow.data.mangadex.chapter_metadata.MangaDexChapterMetadata
import com.example.shikiflow.presentation.viewmodel.manga.read.MangaChapterTranslationViewModel
import com.example.shikiflow.utils.FlagConverter
import com.example.shikiflow.utils.Resource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChapterTranslationsScreen(
    chapterTranslationIds: List<String>,
    title: String,
    chapterNumber: String,
    navOptions: MangaReadNavOptions,
    chapterTranslationsViewModel: MangaChapterTranslationViewModel = hiltViewModel()
) {

    val chapterTranslations = chapterTranslationsViewModel.chapterTranslations.collectAsState()

    val lazyListState = rememberLazyListState()
    val isAtTop by remember {
        derivedStateOf {
            lazyListState.firstVisibleItemIndex == 0 &&
                    lazyListState.firstVisibleItemScrollOffset == 0
        }
    }

    LaunchedEffect(chapterTranslationIds) {
        chapterTranslationsViewModel.getChapterTranslations(chapterTranslationIds)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Chapter $chapterNumber — $title",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = { navOptions.navigateBack() },
                        enabled = false
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
        when(val translations = chapterTranslations.value) {
            is Resource.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) { CircularProgressIndicator() }
            }
            is Resource.Success -> {
                LazyColumn(
                    state = lazyListState,
                    modifier = Modifier.fillMaxSize().padding(
                        top = paddingValues.calculateTopPadding(),
                        start = paddingValues.calculateStartPadding(LayoutDirection.Ltr),
                        end = paddingValues.calculateEndPadding(LayoutDirection.Ltr),
                    ), contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val translations = translations.data?.sortedBy {
                        it.attributes.publishAt
                    } ?: emptyList()

                    items(translations.size) { index ->
                        ChapterTranslationItem(
                            mangaDexChapter = translations[index],
                            onTranslationClick = { translationId ->
                                navOptions.navigateToChapter(translationId)
                            }
                        )
                    }
                }
            }
            is Resource.Error -> { /**/ }
        }
    }
}

@Composable
private fun ChapterTranslationItem(
    mangaDexChapter: MangaDexChapterMetadata,
    onTranslationClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp))
            .clickable { onTranslationClick(mangaDexChapter.id) }
            .padding(horizontal = 6.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = FlagConverter.getFlag(mangaDexChapter.attributes.translatedLanguage),
            style = MaterialTheme.typography.titleLarge
        )
        Text(
            text = if(!mangaDexChapter.attributes.title.isNullOrEmpty()) mangaDexChapter.attributes.title
                else "Ch. ${mangaDexChapter.attributes.chapter}",
            style = MaterialTheme.typography.bodyLarge.copy(
                fontWeight = FontWeight.SemiBold
            ),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}