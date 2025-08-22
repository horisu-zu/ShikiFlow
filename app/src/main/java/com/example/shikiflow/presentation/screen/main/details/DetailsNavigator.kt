package com.example.shikiflow.presentation.screen.main.details

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
import com.example.shikiflow.data.tracks.MediaType
import com.example.shikiflow.presentation.screen.MediaNavOptions
import com.example.shikiflow.presentation.screen.main.SimilarMediaScreen
import com.example.shikiflow.presentation.screen.main.details.anime.AnimeDetailsScreen
import com.example.shikiflow.presentation.screen.main.details.character.CharacterDetailsScreen
import com.example.shikiflow.presentation.screen.main.details.common.CommentsScreen
import com.example.shikiflow.presentation.screen.main.details.common.CommentsScreenMode
import com.example.shikiflow.presentation.screen.main.details.common.ExternalLinksScreen
import com.example.shikiflow.presentation.screen.main.details.manga.MangaDetailsScreen
import com.example.shikiflow.presentation.screen.main.details.manga.read.MangaReadNavigator

@Composable
fun DetailsNavigator(
    currentUserData: CurrentUserQuery.Data?,
    mediaId: String,
    mediaType: MediaType,
    source: String
) {
    val detailsBackstack = rememberNavBackStack(when(mediaType) {
        MediaType.ANIME -> DetailsNavRoute.AnimeDetails(mediaId)
        MediaType.MANGA -> DetailsNavRoute.MangaDetails(mediaId)
    })
    val options = object : MediaNavOptions {
        override fun navigateToCharacterDetails(characterId: String) {
            detailsBackstack.add(DetailsNavRoute.CharacterDetails(characterId))
        }

        override fun navigateToAnimeDetails(animeId: String) {
            detailsBackstack.add(DetailsNavRoute.AnimeDetails(animeId))
        }

        override fun navigateToMangaDetails(mangaId: String) {
            detailsBackstack.add(DetailsNavRoute.MangaDetails(mangaId))
        }

        override fun navigateToSimilarPage(id: String, title: String, mediaType: MediaType) {
            detailsBackstack.add(DetailsNavRoute.SimilarPage(id, title, mediaType))
        }

        override fun navigateToLinksPage(id: String, mediaType: MediaType) {
            detailsBackstack.add(DetailsNavRoute.ExternalLinks(mediaId, mediaType))
        }

        override fun navigateToMangaRead(mangaDexId: String, title: String, completedChapters: Int) {
            detailsBackstack.add(DetailsNavRoute.MangaRead(mangaDexId, title, completedChapters))
        }

        override fun navigateToComments(screenMode: CommentsScreenMode, id: String) {
            detailsBackstack.add(DetailsNavRoute.Comments(screenMode, id))
        }

        override fun navigateBack() {
            detailsBackstack.removeLastOrNull()
        }
    }

    NavDisplay(
        backStack = detailsBackstack,
        onBack = { detailsBackstack.removeLastOrNull() },
        entryProvider = entryProvider {
            entry<DetailsNavRoute.AnimeDetails> { route ->
                AnimeDetailsScreen(
                    id = route.id,
                    currentUser = currentUserData,
                    navOptions = options,
                    animeDetailsViewModel = hiltViewModel(key = "${source}_${route.id}")
                )
            }
            entry<DetailsNavRoute.MangaDetails> { route ->
                MangaDetailsScreen(
                    id = route.id,
                    navOptions = options,
                    currentUser = currentUserData,
                    mangaDetailsViewModel = hiltViewModel(key = "${source}_${route.id}")
                )
            }
            entry<DetailsNavRoute.CharacterDetails> { route ->
                CharacterDetailsScreen(
                    characterId = route.characterId,
                    navOptions = options,
                    characterDetailsViewModel = hiltViewModel(key = "${source}_${route.characterId}")
                )
            }
            entry<DetailsNavRoute.SimilarPage> { route ->
                SimilarMediaScreen(
                    mediaTitle = route.title,
                    mediaId = route.id,
                    mediaType = route.mediaType,
                    navOptions = options,
                    similarMediaViewModel = hiltViewModel(key = "similar_${route.id}")
                )
            }
            entry<DetailsNavRoute.ExternalLinks> { route ->
                ExternalLinksScreen(
                    mediaId = route.mediaId,
                    mediaType = route.mediaType,
                    navOptions = options,
                    externalLinksViewModel = hiltViewModel(key = "links_${route.mediaId}")
                )
            }
            entry<DetailsNavRoute.MangaRead> { route ->
                MangaReadNavigator(
                    mangaDexId = route.mangaDexId,
                    title = route.title,
                    completedChapters = route.completedChapters,
                    onNavigateBack = { options.navigateBack() },
                    mangaChaptersViewModel = hiltViewModel(key = "read_${route.mangaDexId}")
                )
            }
            entry<DetailsNavRoute.Comments> { route ->
                CommentsScreen(
                    screenMode = route.screenMode,
                    id = route.id,
                    navOptions = options,
                    commentViewModel = hiltViewModel(key = "comments_${route.screenMode}_${route.id}")
                )
            }
        },
        transitionSpec = {
            slideInHorizontally(
                initialOffsetX = { it },
                animationSpec = tween(300)
            ) togetherWith ExitTransition.KeepUntilTransitionsFinished
        }
    )
}