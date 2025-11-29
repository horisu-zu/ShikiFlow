package com.example.shikiflow.presentation.screen.main.details.manga.read

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Person
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.shikiflow.R
import com.example.shikiflow.domain.model.mangadex.chapter_metadata.ChapterMetadata
import com.example.shikiflow.presentation.common.ErrorItem
import com.example.shikiflow.presentation.common.TextWithIcon
import com.example.shikiflow.presentation.screen.LocalBottomBarController
import com.example.shikiflow.presentation.viewmodel.manga.read.MangaChapterTranslationViewModel
import com.example.shikiflow.utils.FlagConverter
import com.example.shikiflow.utils.IconResource
import com.example.shikiflow.utils.Resource
import com.example.shikiflow.utils.WebIntent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChapterTranslationsScreen(
    chapterTranslationIds: List<String>,
    title: String,
    chapterNumber: String,
    navOptions: MangaReadNavOptions,
    chapterTranslationsViewModel: MangaChapterTranslationViewModel = hiltViewModel()
) {
    val context = LocalContext.current
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
            Column {
                TopAppBar(
                    title = {
                        Text(
                            text = stringResource(
                                R.string.translation_chapter_label,
                                chapterNumber,
                                title
                            ),
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
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back to Main"
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = if(isAtTop) MaterialTheme.colorScheme.background
                            else MaterialTheme.colorScheme.surface
                    )
                )
                HorizontalDivider()
            }
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
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(
                            top = paddingValues.calculateTopPadding(),
                            start = paddingValues.calculateStartPadding(LayoutDirection.Ltr),
                            end = paddingValues.calculateEndPadding(LayoutDirection.Ltr),
                        ), contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    translations.data?.let { chapterTranslations ->
                        items(chapterTranslations.size) { index ->
                            ChapterTranslationItem(
                                mangaDexChapter = chapterTranslations[index],
                                onTranslationClick = { translationId, chapterTitle ->
                                    chapterTranslations[index].externalUrl?.let { externalUrl ->
                                        WebIntent.openUrlCustomTab(context, externalUrl)
                                    } ?: navOptions.navigateToChapter(
                                        mangaDexChapterId = translationId,
                                        title = chapterTitle,
                                        chapterNumber = chapterNumber
                                    )
                                }
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
                        message = translations.message ?: stringResource(R.string.common_error),
                        buttonLabel = stringResource(R.string.common_retry),
                        onButtonClick = {
                            chapterTranslationsViewModel.getChapterTranslations(chapterTranslationIds)
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun ChapterTranslationItem(
    mangaDexChapter: ChapterMetadata,
    onTranslationClick: (String, String?) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable { onTranslationClick(mangaDexChapter.id, mangaDexChapter.title) }
            .padding(horizontal = 6.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp, Alignment.Top)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = FlagConverter.getFlag(mangaDexChapter.translatedLanguage),
                style = MaterialTheme.typography.titleLarge
            )
            Text(
                text = if(!mangaDexChapter.title.isNullOrEmpty()) mangaDexChapter.title
                    else stringResource(R.string.chapter_empty_title, mangaDexChapter.chapterNumber ?: "?"),
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.SemiBold
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.Start)
        ) {
            if(mangaDexChapter.scanlationGroups.isNotEmpty()) {
                mangaDexChapter.scanlationGroups.forEach { scanlationGroup ->
                    TextWithIcon(
                        text = scanlationGroup.name,
                        iconResources = listOf(
                            if(scanlationGroup.isOfficial) {
                                IconResource.Vector(imageVector = Icons.Default.Check)
                            } else {
                                IconResource.Vector(imageVector = Icons.Default.Person)
                            }
                        ),
                        style = MaterialTheme.typography.bodyMedium,
                        color = if(scanlationGroup.isOfficial) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.75f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            } else {
                mangaDexChapter.uploaderNickname?.let { uploaderName ->
                    TextWithIcon(
                        text = uploaderName,
                        iconResources = listOf(
                            IconResource.Vector(imageVector = Icons.Default.Person)
                        ),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.75f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}