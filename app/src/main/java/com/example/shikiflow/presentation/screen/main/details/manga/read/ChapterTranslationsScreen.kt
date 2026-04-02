package com.example.shikiflow.presentation.screen.main.details.manga.read

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
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
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.shikiflow.R
import com.example.shikiflow.domain.model.mangadex.chapter_metadata.ChapterMetadata
import com.example.shikiflow.presentation.common.ErrorItem
import com.example.shikiflow.presentation.common.TextWithIcon
import com.example.shikiflow.presentation.viewmodel.manga.read.translations.MangaChapterTranslationViewModel
import com.example.shikiflow.utils.FlagConverter
import com.example.shikiflow.utils.IconResource
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
    val uiState by chapterTranslationsViewModel.uiState.collectAsState()

    val lazyListState = rememberLazyListState()
    val isAtTop by remember {
        derivedStateOf {
            lazyListState.firstVisibleItemIndex == 0 &&
            lazyListState.firstVisibleItemScrollOffset == 0
        }
    }

    LaunchedEffect(chapterTranslationIds) {
        chapterTranslationsViewModel.setChapterIds(chapterTranslationIds)
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
                            else MaterialTheme.colorScheme.surfaceContainer
                    )
                )
                HorizontalDivider()
            }
        }, modifier = Modifier.fillMaxSize()
    ) { paddingValues ->
        if(uiState.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) { CircularProgressIndicator() }
        } else if(uiState.errorMessage != null) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                ErrorItem(
                    message = uiState.errorMessage ?: stringResource(R.string.common_error),
                    buttonLabel = stringResource(R.string.common_retry),
                    onButtonClick = { chapterTranslationsViewModel.onRefresh() }
                )
            }
        } else {
            LazyColumn(
                state = lazyListState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = paddingValues.calculateTopPadding()),
                contentPadding = PaddingValues(
                    start = 12.dp,
                    end = 12.dp,
                    top = 8.dp,
                    bottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
                ),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                uiState.chapterTranslations.let { chapterTranslations ->
                    items(chapterTranslations.size) { index ->
                        ChapterTranslationItem(
                            mangaDexChapter = chapterTranslations[index],
                            onTranslationClick = { chapterUiData ->
                                chapterTranslations[index].externalUrl?.let { externalUrl ->
                                    WebIntent.openUrlCustomTab(context, externalUrl)
                                } ?: navOptions.navigateToChapter(
                                    chapterUiData = chapterUiData
                                )
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ChapterTranslationItem(
    mangaDexChapter: ChapterMetadata,
    onTranslationClick: (ChapterUiData) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable { onTranslationClick(
                ChapterUiData(
                    title = mangaDexChapter.title,
                    mangaId = mangaDexChapter.mangaId,
                    chapterId = mangaDexChapter.chapterId,
                    scanlationGroupIds = mangaDexChapter.scanlationGroups.map { it.id },
                    chapterNumber = mangaDexChapter.chapterNumber,
                    uploader = mangaDexChapter.uploader?.id
                )
            ) }
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
                    else stringResource(R.string.chapter_short_title, mangaDexChapter.chapterNumber ?: "?"),
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
                                IconResource.Drawable(resId = R.drawable.ic_group)
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
                mangaDexChapter.uploader?.let { uploader ->
                    TextWithIcon(
                        text = uploader.username,
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