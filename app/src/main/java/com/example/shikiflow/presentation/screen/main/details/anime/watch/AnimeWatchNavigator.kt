package com.example.shikiflow.presentation.screen.main.details.anime.watch

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation3.runtime.entry
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay

@Composable
fun AnimeWatchNavigator(
    title: String,
    shikimoriId: String,
    completedEpisodes: Int,
    onNavigateBack: () -> Unit,
    onEpisodeNavigate: (String, Int) -> Unit,
    source: String
) {
    val watchBackstack = rememberNavBackStack(AnimeWatchNavRoute.TranslationSelect(shikimoriId))

    val options = object : AnimeWatchNavOptions {
        override fun navigateToEpisodeSelection(link: String, episodesCount: Int) {
            watchBackstack.add(AnimeWatchNavRoute.EpisodeSelection(link, episodesCount))
        }

        override fun navigateToEpisode(link: String, serialNum: Int) {
            watchBackstack.add(AnimeWatchNavRoute.EpisodeScreen(link, serialNum))
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
                    title = title,
                    shikimoriId = route.shikimoriId,
                    navOptions = options,
                    onNavigateBack = onNavigateBack,
                    animeTranslationsViewModel = hiltViewModel(key = "${source}_translations")
                )
            }
            entry<AnimeWatchNavRoute.EpisodeSelection> { route ->
                EpisodeSelectionScreen(
                    title = title,
                    episodesCount = route.episodesCount,
                    link = route.link,
                    completedEpisodes = completedEpisodes,
                    navOptions = options,
                    onEpisodeNavigate = onEpisodeNavigate
                )
            }
        }
    )
}