package com.example.shikiflow.presentation.screen.main

import android.util.Log
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation3.runtime.entry
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.example.graphql.CurrentUserQuery
import com.example.shikiflow.data.tracks.MediaType
import com.example.shikiflow.presentation.screen.MainScreenNavRoute
import com.example.shikiflow.presentation.screen.MediaNavOptions
import com.example.shikiflow.presentation.screen.main.details.anime.AnimeDetailsScreen
import com.example.shikiflow.presentation.screen.main.details.character.CharacterDetailsScreen
import com.example.shikiflow.presentation.screen.main.details.manga.MangaDetailsScreen
import com.example.shikiflow.utils.AppSettingsManager

@Composable
fun MainScreenNavigator(
    appSettingsManager: AppSettingsManager,
    currentUserData: CurrentUserQuery.Data?
) {
    val mainScreenBackstack = rememberNavBackStack(MainScreenNavRoute.MainTracks)
    val options = object : MediaNavOptions {
        override fun navigateToCharacterDetails(characterId: String) {
            mainScreenBackstack.add(MainScreenNavRoute.CharacterDetails(characterId))
        }

        override fun navigateToAnimeDetails(animeId: String) {
            mainScreenBackstack.add(MainScreenNavRoute.AnimeDetails(animeId))
        }

        override fun navigateToMangaDetails(mangaId: String) {
            mainScreenBackstack.add(MainScreenNavRoute.MangaDetails(mangaId))
        }

        override fun navigateToSimilarPage(id: String, title: String, mediaType: MediaType) {
            mainScreenBackstack.add(MainScreenNavRoute.SimilarPage(id, title, mediaType))
        }

        override fun navigateBack() {
            mainScreenBackstack.removeLastOrNull()
        }
    }

    NavDisplay(
        backStack = mainScreenBackstack,
        onBack = { mainScreenBackstack.removeLastOrNull() },
        entryProvider = entryProvider {
            entry<MainScreenNavRoute.MainTracks> {
                MainScreen(
                    appSettingsManager = appSettingsManager,
                    currentUser = currentUserData,
                    navOptions = options
                )
            }
            entry<MainScreenNavRoute.AnimeDetails>(
                metadata = NavDisplay.transitionSpec {
                    slideInHorizontally(
                        initialOffsetX = { it },
                        animationSpec = tween(300)
                    ) togetherWith ExitTransition.KeepUntilTransitionsFinished
                }
            ) { route ->
                AnimeDetailsScreen(
                    id = route.id,
                    currentUser = currentUserData,
                    navOptions = options,
                    animeDetailsViewModel = hiltViewModel(key = "main_${route.id}")
                )
            }
            entry<MainScreenNavRoute.MangaDetails>(
                metadata = NavDisplay.transitionSpec {
                    slideInHorizontally(
                        initialOffsetX = { it },
                        animationSpec = tween(300)
                    ) togetherWith ExitTransition.KeepUntilTransitionsFinished
                }
            ) { route ->
                MangaDetailsScreen(
                    id = route.id,
                    navOptions = options,
                    currentUser = currentUserData,
                    mangaDetailsViewModel = hiltViewModel(key = "main_${route.id}")
                )
            }
            entry<MainScreenNavRoute.CharacterDetails>(
                metadata = NavDisplay.transitionSpec {
                    slideInHorizontally(
                        initialOffsetX = { it },
                        animationSpec = tween(300)
                    ) togetherWith ExitTransition.KeepUntilTransitionsFinished
                }
            ) { route ->
                CharacterDetailsScreen(
                    characterId = route.characterId,
                    navOptions = options,
                    characterDetailsViewModel = hiltViewModel(key = "main_${route.characterId}")
                )
            }
            entry<MainScreenNavRoute.SimilarPage>(
                metadata = NavDisplay.transitionSpec {
                    slideInHorizontally(
                        initialOffsetX = { it },
                        animationSpec = tween(300)
                    ) togetherWith ExitTransition.KeepUntilTransitionsFinished
                }
            ) { route ->
                SimilarMediaScreen(
                    mediaTitle = route.title,
                    mediaId = route.id,
                    mediaType = route.mediaType,
                    navOptions = options,
                    similarMediaViewModel = hiltViewModel(key = "main_${route.id}")
                )
            }
        }
    )
}
