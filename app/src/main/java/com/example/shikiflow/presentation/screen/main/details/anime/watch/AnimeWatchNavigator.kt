package com.example.shikiflow.presentation.screen.main.details.anime.watch

import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.entry
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay

@Composable
fun AnimeWatchNavigator(
    title: String,
    shikimoriId: String,
    completedEpisodes: Int,
    onNavigateBack: () -> Unit
) {
    val watchBackstack = rememberNavBackStack(AnimeWatchNavRoute.TranslationSelect(title, shikimoriId))

    val options = object : AnimeWatchNavOptions {
        override fun navigateToEpisodeSelection(link: String) {
            watchBackstack.add(AnimeWatchNavRoute.EpisodeSelection(link))
        }

        override fun navigateToEpisode(hlsUrl: String) {
            watchBackstack.add(AnimeWatchNavRoute.EpisodeScreen(hlsUrl))
        }

        override fun navigateBack() {
            if(watchBackstack.size > 1) {
                watchBackstack.removeLastOrNull()
            }
        }
    }

    NavDisplay(
        backStack = watchBackstack,
        onBack = { watchBackstack.removeLastOrNull() },
        entryProvider = entryProvider {
            entry<AnimeWatchNavRoute.TranslationSelect> { route ->
                AnimeTranslationSelectScreen(
                    title = route.title,
                    shikimoriId = route.shikimoriId,
                    navOptions = options,
                    onNavigateBack = onNavigateBack
                )
            }
            entry<AnimeWatchNavRoute.EpisodeSelection> { route ->
                EpisodeSelectionScreen(
                    link = route.link,
                    completedEpisodes = completedEpisodes,
                    navOptions = options
                )
            }
            entry<AnimeWatchNavRoute.EpisodeScreen> { route ->
                EpisodeScreen(
                    hlsUrl = route.hlsUrl,
                    navOptions = options
                )
            }
        }
    )
}