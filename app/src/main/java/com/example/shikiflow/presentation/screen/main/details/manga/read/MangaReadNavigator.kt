package com.example.shikiflow.presentation.screen.main.details.manga.read

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay

@Composable
fun MangaReadNavigator(
    mangaDexIds: List<String>,
    title: String,
    completedChapters: Int,
    onNavigateBack: () -> Unit,
    onChapterScreen: (Boolean) -> Unit,
    source: String
) {
    val mangaReadBackstack = when(mangaDexIds.size) {
        1 -> rememberNavBackStack(MangaReadNavRoute.ChaptersScreen(
            mangaDexId = mangaDexIds[0],
            title = title,
            source = ChaptersScreenSource.AUTOMATED
        ))
        else -> {
            rememberNavBackStack(MangaReadNavRoute.MangaSelectionScreen(
                mangaDexIds = mangaDexIds,
                title = title
            ))
        }
    }

    val isChapterScreen by remember {
        derivedStateOf {
            mangaReadBackstack.last() is MangaReadNavRoute.ChapterScreen
        }
    }

    LaunchedEffect(isChapterScreen) {
        onChapterScreen(isChapterScreen)
    }

    val navOptions = object : MangaReadNavOptions {
        override fun navigateToChapters(
            mangaDexId: String,
            title: String,
            source: ChaptersScreenSource
        ) {
            mangaReadBackstack.add(MangaReadNavRoute.ChaptersScreen(mangaDexId, title, source))
        }

        override fun navigateToChapterTranslations(chapterTranslationIds: List<String>, chapterNumber: String) {
            mangaReadBackstack.add(MangaReadNavRoute.ChapterTranslationsScreen(
                chapterTranslationIds, title, chapterNumber
            ))
        }

        override fun navigateToChapter(mangaDexChapterId: String, title: String?, chapterNumber: String) {
            mangaReadBackstack.add(MangaReadNavRoute.ChapterScreen(mangaDexChapterId, title, chapterNumber))
        }

        override fun navigateBack() {
            if(mangaReadBackstack.size > 1) {
                mangaReadBackstack.removeLastOrNull()
            }
        }
    }

    NavDisplay(
        backStack = mangaReadBackstack,
        onBack = { mangaReadBackstack.removeLastOrNull() },
        entryProvider = entryProvider {
            entry<MangaReadNavRoute.MangaSelectionScreen> { route ->
                MangaSelectionScreen(
                    mangaDexIds = route.mangaDexIds,
                    title = route.title,
                    navOptions = navOptions,
                    onNavigateBack = onNavigateBack,
                    mangaSelectionViewModel = hiltViewModel(key = "${source}_selection")
                )
            }
            entry<MangaReadNavRoute.ChaptersScreen> { route ->
                MangaChaptersScreen(
                    mangaDexId = route.mangaDexId,
                    title = route.title,
                    completedChapters = completedChapters,
                    navOptions = navOptions,
                    onNavigateBack = onNavigateBack,
                    mangaChaptersViewModel = hiltViewModel(key = "${source}_chapters"),
                    navigationSource = route.source
                )
            }
            entry<MangaReadNavRoute.ChapterTranslationsScreen> { route ->
                ChapterTranslationsScreen(
                    chapterTranslationIds = route.chapterTranslationIds,
                    title = route.title,
                    chapterNumber = route.chapterNumber,
                    navOptions = navOptions,
                    chapterTranslationsViewModel = hiltViewModel(key = "${source}_translations")
                )
            }
            entry<MangaReadNavRoute.ChapterScreen> { route ->
                ChapterScreen(
                    mangaDexChapterId = route.mangaDexChapterId,
                    chapterNumber = route.chapterNumber,
                    title = route.title,
                    navOptions = navOptions,
                    chapterViewModel = hiltViewModel(key = "${source}_chapter")
                )
            }
        }
    )
}

enum class ChaptersScreenSource {
    AUTOMATED,
    MANUAL
}