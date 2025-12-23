package com.example.shikiflow.presentation.screen.main.details

import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.navigation.BackNavigationBehavior
import androidx.compose.material3.adaptive.navigation3.ListDetailSceneStrategy
import androidx.compose.material3.adaptive.navigation3.rememberListDetailSceneStrategy
import androidx.compose.runtime.Composable
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.example.shikiflow.domain.model.tracks.MediaType
import com.example.shikiflow.domain.model.user.User
import com.example.shikiflow.presentation.screen.main.SimilarMediaScreen
import com.example.shikiflow.presentation.screen.main.details.anime.AnimeDetailsScreen
import com.example.shikiflow.presentation.screen.main.details.anime.studio.StudioScreen
import com.example.shikiflow.presentation.screen.main.details.anime.watch.AnimeWatchNavigator
import com.example.shikiflow.presentation.screen.main.details.character.CharacterDetailsScreen
import com.example.shikiflow.presentation.screen.main.details.common.CommentsScreen
import com.example.shikiflow.presentation.screen.main.details.common.CommentsScreenMode
import com.example.shikiflow.presentation.screen.main.details.common.ExternalLinksScreen
import com.example.shikiflow.presentation.screen.main.details.manga.MangaDetailsScreen
import com.example.shikiflow.presentation.screen.main.details.manga.read.MangaReadNavigator
import com.example.shikiflow.presentation.screen.main.details.person.PersonScreen
import com.example.shikiflow.utils.Converter.EntityType

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun DetailsNavigator(
    currentUserData: User?,
    mediaId: String,
    mediaType: MediaType,
    source: String,
) {
    val twoPaneSceneStrategy = rememberListDetailSceneStrategy<NavKey>(
        backNavigationBehavior = BackNavigationBehavior.PopUntilContentChange
    )
    //val sceneStrategy = rememberTwoPaneSceneStrategy<NavKey>()
    val detailsBackstack = rememberNavBackStack(when(mediaType) {
        MediaType.ANIME -> DetailsNavRoute.AnimeDetails(mediaId)
        MediaType.MANGA -> DetailsNavRoute.MangaDetails(mediaId)
    })

    val options = object : MediaNavOptions {

        //Added this function to prevent details pane appearing after returning from other screens
        fun twoPaneNavigate(detailsNavRoute: DetailsNavRoute) {
            val index = detailsBackstack.indexOfLast { it !is DetailsNavRoute.SimilarPage && it !is DetailsNavRoute.ExternalLinks }

            repeat(detailsBackstack.size - index - 1) {
                detailsBackstack.removeAt(detailsBackstack.lastIndex)
            }

            detailsBackstack.add(detailsNavRoute)
        }

        override fun navigateToCharacterDetails(characterId: String) {
            twoPaneNavigate(DetailsNavRoute.CharacterDetails(characterId))
        }

        override fun navigateToAnimeDetails(animeId: String) {
            twoPaneNavigate(DetailsNavRoute.AnimeDetails(animeId))
        }

        override fun navigateToMangaDetails(mangaId: String) {
            twoPaneNavigate(DetailsNavRoute.MangaDetails(mangaId))
        }

        override fun navigateToSimilarPage(id: String, title: String, mediaType: MediaType) {
            twoPaneNavigate(DetailsNavRoute.SimilarPage(id, title, mediaType))
        }

        override fun navigateToLinksPage(id: String, mediaType: MediaType) {
            twoPaneNavigate(DetailsNavRoute.ExternalLinks(mediaId, mediaType))
        }

        override fun navigateToMangaRead(mangaDexIds: List<String>, title: String, completedChapters: Int) {
            twoPaneNavigate(DetailsNavRoute.MangaRead(mangaDexIds, title, completedChapters))
        }

        override fun navigateToComments(screenMode: CommentsScreenMode, id: String) {
            twoPaneNavigate(DetailsNavRoute.Comments(screenMode, id))
        }

        override fun navigateToPerson(personId: String) {
            twoPaneNavigate(DetailsNavRoute.Person(personId))
        }

        override fun navigateToAnimeWatch(title: String, shikimoriId: String, completedEpisodes: Int) {
            twoPaneNavigate(DetailsNavRoute.AnimeWatch(title, shikimoriId, completedEpisodes))
        }

        override fun navigateToStudio(id: String, studioName: String) {
            twoPaneNavigate(DetailsNavRoute.Studio(id, studioName))
        }

        override fun navigateByEntity(entityType: EntityType, id: String) {
            when (entityType) {
                EntityType.CHARACTER -> {
                    navigateToCharacterDetails(id)
                }
                EntityType.PERSON -> {
                    navigateToPerson(id)
                }
                EntityType.ANIME -> {
                    navigateToAnimeDetails(id)
                }
                EntityType.MANGA, EntityType.RANOBE -> {
                    navigateToMangaDetails(id)
                }
                EntityType.COMMENT -> {
                    navigateToComments(CommentsScreenMode.COMMENT, id)
                }
            }
        }

        override fun navigateBack() {
            if(detailsBackstack.size > 1) detailsBackstack.removeLastOrNull()
        }
    }

    NavDisplay(
        backStack = detailsBackstack,
        sceneStrategy = twoPaneSceneStrategy,
        entryProvider = entryProvider {
            entry<DetailsNavRoute.AnimeDetails>(
                metadata = ListDetailSceneStrategy.listPane()
            ) { route ->
                AnimeDetailsScreen(
                    id = route.id,
                    userId = currentUserData?.id,
                    navOptions = options,
                    animeDetailsViewModel = hiltViewModel(key = source)
                )
            }
            entry<DetailsNavRoute.MangaDetails>(
                metadata = ListDetailSceneStrategy.listPane()
            ) { route ->
                MangaDetailsScreen(
                    id = route.id,
                    userId = currentUserData?.id,
                    navOptions = options,
                    mangaDetailsViewModel = hiltViewModel(key = source)
                )
            }
            entry<DetailsNavRoute.CharacterDetails> { route ->
                CharacterDetailsScreen(
                    characterId = route.characterId,
                    navOptions = options,
                    characterDetailsViewModel = hiltViewModel(key = source)
                )
            }
            entry<DetailsNavRoute.SimilarPage>(
                metadata = ListDetailSceneStrategy.detailPane()
            ) { route ->
                SimilarMediaScreen(
                    mediaTitle = route.title,
                    mediaId = route.id,
                    mediaType = route.mediaType,
                    navOptions = options,
                    similarMediaViewModel = hiltViewModel(key = source)
                )
            }
            entry<DetailsNavRoute.ExternalLinks>(
                metadata = ListDetailSceneStrategy.detailPane()
            ) { route ->
                ExternalLinksScreen(
                    mediaId = route.mediaId,
                    mediaType = route.mediaType,
                    navOptions = options,
                    externalLinksViewModel = hiltViewModel(key = source)
                )
            }
            entry<DetailsNavRoute.MangaRead> { route ->
                MangaReadNavigator(
                    mangaDexIds = route.mangaDexIds,
                    title = route.title,
                    completedChapters = route.completedChapters,
                    onNavigateBack = { options.navigateBack() },
                    source = source
                )
            }
            entry<DetailsNavRoute.Comments> { route ->
                CommentsScreen(
                    screenMode = route.screenMode,
                    id = route.id,
                    navOptions = options,
                    commentViewModel = hiltViewModel(key = source)
                )
            }
            entry<DetailsNavRoute.Person> { route ->
                PersonScreen(
                    personId = route.personId,
                    navOptions = options,
                    personViewModel = hiltViewModel(key = source)
                )
            }
            entry<DetailsNavRoute.AnimeWatch> { route ->
                AnimeWatchNavigator(
                    title = route.title,
                    shikimoriId = route.shikimoriId,
                    completedEpisodes = route.completedEpisodes,
                    onNavigateBack = { options.navigateBack() },
                    source = source
                )
            }
            entry<DetailsNavRoute.Studio> { route ->
                StudioScreen(
                    id = route.id,
                    studioName = route.studioName,
                    onNavigateBack = { options.navigateBack() },
                    onMediaNavigate = { animeId ->
                        options.navigateToAnimeDetails(animeId)
                    },
                    studioViewModel = hiltViewModel(key = source)
                )
            }
        },
        transitionSpec = {
            slideInHorizontally(
                initialOffsetX = { it },
                animationSpec = tween(250)
            ) togetherWith ExitTransition.KeepUntilTransitionsFinished
        },
        popTransitionSpec = { fadeIn() togetherWith fadeOut() },
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator()
        )
    )
}