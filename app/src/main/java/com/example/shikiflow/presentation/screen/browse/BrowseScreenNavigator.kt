package com.example.shikiflow.presentation.screen.browse

import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation3.runtime.entry
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.example.graphql.CurrentUserQuery
import com.example.shikiflow.data.anime.BrowseType
import com.example.shikiflow.data.tracks.MediaType
import com.example.shikiflow.presentation.screen.main.SimilarMediaScreen
import com.example.shikiflow.presentation.screen.main.details.anime.AnimeDetailsScreen
import com.example.shikiflow.presentation.screen.main.details.character.CharacterDetailsScreen
import com.example.shikiflow.presentation.screen.main.details.manga.MangaDetailsScreen

@Composable
fun BrowseScreenNavigator(
    currentUserData: CurrentUserQuery.Data?
) {
    val browseBackstack = rememberNavBackStack(BrowseNavRoute.BrowseScreen)
    val browseNavOptions = object: BrowseNavOptions {
        override fun navigateToSideScreen(browseType: BrowseType) {
            browseBackstack.add(BrowseNavRoute.SideScreen(browseType))
        }

        override fun navigateToCharacterDetails(characterId: String) {
            browseBackstack.add(BrowseNavRoute.CharacterDetails(characterId))
        }

        override fun navigateToAnimeDetails(animeId: String) {
            browseBackstack.add(BrowseNavRoute.AnimeDetails(animeId))
        }

        override fun navigateToMangaDetails(mangaId: String) {
            browseBackstack.add(BrowseNavRoute.MangaDetails(mangaId))
        }

        override fun navigateToSimilarPage(id: String, title: String, mediaType: MediaType) {
            browseBackstack.add(BrowseNavRoute.SimilarPage(id, title, mediaType))
        }

        override fun navigateBack() { browseBackstack.removeLastOrNull() }
    }

    NavDisplay(
        backStack = browseBackstack,
        onBack = { browseBackstack.removeLastOrNull() },
        entryProvider = entryProvider {
            entry<BrowseNavRoute.BrowseScreen> {
                BrowseScreen(
                    browseNavOptions = browseNavOptions
                )
            }
            entry<BrowseNavRoute.SideScreen> { route ->
                BrowseSideScreen(
                    browseType = route.browseType,
                    navOptions = browseNavOptions,
                    onBackNavigate = { browseBackstack.removeLastOrNull() }
                )
            }
            entry<BrowseNavRoute.AnimeDetails>(
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
                    navOptions = browseNavOptions,
                    animeDetailsViewModel = hiltViewModel(key = "browse_${route.id}")
                )
            }
            entry<BrowseNavRoute.MangaDetails>(
                metadata = NavDisplay.transitionSpec {
                    slideInHorizontally(
                        initialOffsetX = { it },
                        animationSpec = tween(300)
                    ) togetherWith ExitTransition.KeepUntilTransitionsFinished
                }
            ) { route ->
                MangaDetailsScreen(
                    id = route.id,
                    navOptions = browseNavOptions,
                    currentUser = currentUserData,
                    mangaDetailsViewModel = hiltViewModel(key = "browse_${route.id}")
                )
            }
            entry<BrowseNavRoute.CharacterDetails>(
                metadata = NavDisplay.transitionSpec {
                    slideInHorizontally(
                        initialOffsetX = { it },
                        animationSpec = tween(300)
                    ) togetherWith ExitTransition.KeepUntilTransitionsFinished
                }
            ) { route ->
                CharacterDetailsScreen(
                    characterId = route.characterId,
                    navOptions = browseNavOptions,
                    characterDetailsViewModel = hiltViewModel(key = "browse_${route.characterId}")
                )
            }
            entry<BrowseNavRoute.SimilarPage>(
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
                    navOptions = browseNavOptions,
                    similarMediaViewModel = hiltViewModel(key = "browse_${route.id}")
                )
            }
        }
    )
}