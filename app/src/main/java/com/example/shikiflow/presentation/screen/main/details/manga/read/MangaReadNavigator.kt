package com.example.shikiflow.presentation.screen.main.details.manga.read

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation3.runtime.entry
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.example.shikiflow.presentation.viewmodel.manga.read.MangaChaptersViewModel

@Composable
fun MangaReadNavigator(
    mangaDexIds: List<String>,
    title: String,
    completedChapters: Int,
    onNavigateBack: () -> Unit,
    mangaChaptersViewModel: MangaChaptersViewModel = hiltViewModel()
) {
    val mangaReadBackstack = when(mangaDexIds.size) {
        1 -> rememberNavBackStack(MangaReadNavRoute.ChaptersScreen(
            mangaDexId = mangaDexIds[0],
            title = title,
            completedChapters = completedChapters,
            source = ChaptersScreenSource.AUTOMATED
        ))
        else -> {
            rememberNavBackStack(MangaReadNavRoute.MangaSelectionScreen(
                mangaDexIds = mangaDexIds,
                title = title,
                completedChapters = completedChapters
            ))
        }
    }

    val navOptions = object : MangaReadNavOptions {
        override fun navigateToChapters(
            mangaDexId: String,
            title: String,
            completedChapters: Int,
            source: ChaptersScreenSource
        ) {
            mangaReadBackstack.add(MangaReadNavRoute.ChaptersScreen(mangaDexId, title, completedChapters, source))
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
                    completedChapters = route.completedChapters,
                    navOptions = navOptions,
                    onNavigateBack = onNavigateBack
                )
            }
            entry<MangaReadNavRoute.ChaptersScreen> { route ->
                MangaChaptersScreen(
                    mangaDexId = route.mangaDexId,
                    title = route.title,
                    completedChapters = route.completedChapters,
                    navOptions = navOptions,
                    onNavigateBack = onNavigateBack,
                    mangaChaptersViewModel = mangaChaptersViewModel,
                    navigationSource = route.source
                )
            }
            entry<MangaReadNavRoute.ChapterTranslationsScreen> { route ->
                ChapterTranslationsScreen(
                    chapterTranslationIds = route.chapterTranslationIds,
                    title = route.title,
                    chapterNumber = route.chapterNumber,
                    navOptions = navOptions
                )
            }
            entry<MangaReadNavRoute.ChapterScreen> { route ->
                ChapterScreen(
                    mangaDexChapterId = route.mangaDexChapterId,
                    chapterNumber = route.chapterNumber,
                    title = route.title,
                    navOptions = navOptions
                )
            }
        }
    )
}

enum class ChaptersScreenSource {
    AUTOMATED,
    MANUAL
}