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
    mangaDexId: String,
    title: String,
    completedChapters: Int,
    onNavigateBack: () -> Unit,
    mangaChaptersViewModel: MangaChaptersViewModel = hiltViewModel()
) {
    val mangaReadBackstack = rememberNavBackStack(MangaReadNavRoute.ChaptersScreen(
        mangaDexId = mangaDexId,
        title = title,
        completedChapters = completedChapters
    ))

    val navOptions = object : MangaReadNavOptions {
        override fun navigateToChapterTranslations(chapterTranslationIds: List<String>, chapterNumber: String) {
            mangaReadBackstack.add(MangaReadNavRoute.ChapterTranslationsScreen(
                chapterTranslationIds, title, chapterNumber
            ))
        }

        override fun navigateToChapter(mangaDexChapterId: String) {
            mangaReadBackstack.add(MangaReadNavRoute.ChapterScreen(mangaDexChapterId))
        }

        override fun navigateBack() {
            mangaReadBackstack.removeLastOrNull()
        }
    }

    NavDisplay(
        backStack = mangaReadBackstack,
        onBack = { mangaReadBackstack.removeLastOrNull() },
        entryProvider = entryProvider {
            entry<MangaReadNavRoute.ChaptersScreen> { route ->
                MangaChaptersScreen(
                    mangaDexId = route.mangaDexId,
                    title = route.title,
                    completedChapters = route.completedChapters,
                    navOptions = navOptions,
                    onNavigateBack = onNavigateBack,
                    mangaChaptersViewModel = mangaChaptersViewModel
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
                    mangaDexChapterId = route.mangaDexChapterId
                )
            }
        }
    )
}