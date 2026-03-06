package com.example.shikiflow.presentation.screen.main.details.anime.watch

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.example.shikiflow.presentation.screen.LocalBottomBarController
import com.example.shikiflow.presentation.screen.main.details.anime.watch.player.EpisodeScreen
import com.example.shikiflow.presentation.screen.main.details.anime.watch.player.PlayerNavigate

@Composable
fun AnimeWatchNavigator(
    title: String,
    shikimoriId: Int,
    completedEpisodes: Int,
    onNavigateBack: () -> Unit
) {
    val bottomBarController = LocalBottomBarController.current
    val watchBackstack = rememberNavBackStack(AnimeWatchNavRoute.TranslationSelect(shikimoriId))

    val isOnEpisodeScreen by remember {
        derivedStateOf {
            watchBackstack.last() is AnimeWatchNavRoute.EpisodeScreen
        }
    }

    LaunchedEffect(isOnEpisodeScreen) {
        bottomBarController.setVisibility(!isOnEpisodeScreen)
    }

    val options = object : AnimeWatchNavOptions {
        override fun navigateToEpisodeSelection(link: String, translationGroup: String, episodesCount: Int) {
            watchBackstack.add(AnimeWatchNavRoute.EpisodeSelection(link, translationGroup, episodesCount))
        }

        override fun navigateToEpisodeScreen(playerNavigate: PlayerNavigate) {
            watchBackstack.add(AnimeWatchNavRoute.EpisodeScreen(playerNavigate))
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
                    onNavigateBack = onNavigateBack
                )
            }
            entry<AnimeWatchNavRoute.EpisodeSelection> { route ->
                EpisodeSelectionScreen(
                    title = title,
                    translationGroup = route.translationGroup,
                    episodesCount = route.episodesCount,
                    link = route.link,
                    completedEpisodes = completedEpisodes,
                    navOptions = options
                )
            }
            entry<AnimeWatchNavRoute.EpisodeScreen> { route ->
                EpisodeScreen(
                    playerNavigate = route.playerNavigate,
                    navOptions = options
                )
            }
        },
        predictivePopTransitionSpec = {
            slideInHorizontally(
                initialOffsetX = { -it },
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioNoBouncy,
                    stiffness = Spring.StiffnessMediumLow
                )
            ) togetherWith slideOutHorizontally(
                targetOffsetX = { it },
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioNoBouncy,
                    stiffness = Spring.StiffnessMediumLow
                )
            )
        }
    )
}