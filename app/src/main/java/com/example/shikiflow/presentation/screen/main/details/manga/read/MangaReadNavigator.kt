package com.example.shikiflow.presentation.screen.main.details.manga.read

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.example.shikiflow.presentation.screen.LocalBottomBarController

@Composable
fun MangaReadNavigator(
    mangaDexIds: List<String>,
    title: String,
    completedChapters: Int,
    onNavigateBack: () -> Unit,
    source: String
) {
    val bottomBarController = LocalBottomBarController.current
    val mangaReadBackstack = when(mangaDexIds.size) {
        1 -> rememberNavBackStack(MangaReadNavRoute.ChaptersScreen(
            mangaDexId = mangaDexIds[0],
            title = title
        ))
        else -> {
            rememberNavBackStack(MangaReadNavRoute.MangaSelectionScreen(
                mangaDexIds = mangaDexIds,
                title = title
            ))
        }
    }

    val isOnChapterScreen by remember {
        derivedStateOf {
            mangaReadBackstack.last() is MangaReadNavRoute.ChapterScreen
        }
    }

    LaunchedEffect(isOnChapterScreen) {
        bottomBarController.setVisibility(!isOnChapterScreen)
    }

    val navOptions = object : MangaReadNavOptions {
        override fun navigateToChapters(
            mangaDexId: String,
            title: String
        ) {
            mangaReadBackstack.add(MangaReadNavRoute.ChaptersScreen(mangaDexId, title))
        }

        override fun navigateToChapterTranslations(chapterTranslationIds: List<String>, chapterNumber: String) {
            mangaReadBackstack.add(MangaReadNavRoute.ChapterTranslationsScreen(
                chapterTranslationIds, title, chapterNumber
            ))
        }

        override fun navigateToChapter(chapterUiData: ChapterUiData) {
            mangaReadBackstack.add(MangaReadNavRoute.ChapterScreen(chapterUiData))
        }

        override fun navigateBack() {
            if(mangaReadBackstack.size > 1) {
                mangaReadBackstack.removeLastOrNull()
            } else {
                onNavigateBack()
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
                    mangaSelectionViewModel = hiltViewModel(key = "${source}_selection")
                )
            }
            entry<MangaReadNavRoute.ChaptersScreen> { route ->
                MangaChaptersScreen(
                    mangaDexId = route.mangaDexId,
                    title = route.title,
                    completedChapters = completedChapters,
                    navOptions = navOptions,
                    mangaChaptersViewModel = hiltViewModel(key = "${source}_chapters")
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
                    chapterUiData = route.chapterUiData,
                    navOptions = navOptions,
                    chapterViewModel = hiltViewModel(key = "${source}_chapter")
                )
            }
        },
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator()
        )
    )
}