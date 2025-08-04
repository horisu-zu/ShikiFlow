package com.example.shikiflow.presentation.screen.main.details.manga.read

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation3.runtime.entry
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.example.shikiflow.presentation.viewmodel.manga.MangaReadViewModel

@Composable
fun MangaReadNavigator(
    mangaDexId: String,
    title: String,
    completedChapters: Int,
    mangaReadViewModel: MangaReadViewModel = hiltViewModel()
) {
    val mangaReadBackstack = rememberNavBackStack(MangaReadNavRoute.ChaptersScreen(
        mangaDexId = mangaDexId,
        title = title,
        completedChapters = completedChapters
    ))

    val navOptions = object : MangaReadNavOptions {
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
                    mangaReadViewModel = mangaReadViewModel
                )
            }
            // Other Screens
        }
    )
}