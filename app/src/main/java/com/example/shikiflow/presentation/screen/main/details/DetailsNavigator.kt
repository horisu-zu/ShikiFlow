package com.example.shikiflow.presentation.screen.main.details

import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
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
import com.example.shikiflow.domain.model.auth.AuthType
import com.example.shikiflow.domain.model.comment.CommentsScreenMode
import com.example.shikiflow.domain.model.comment.EntityType
import com.example.shikiflow.domain.model.thread.Thread
import com.example.shikiflow.domain.model.tracks.MediaType
import com.example.shikiflow.domain.model.user.User
import com.example.shikiflow.presentation.screen.main.SimilarMediaScreen
import com.example.shikiflow.presentation.screen.main.details.anime.AnimeDetailsScreen
import com.example.shikiflow.presentation.screen.main.details.anime.studio.StudioScreen
import com.example.shikiflow.presentation.screen.main.details.anime.watch.AnimeWatchNavigator
import com.example.shikiflow.presentation.screen.main.details.character.CharacterDetailsScreen
import com.example.shikiflow.presentation.screen.main.details.character.MediaCharactersScreen
import com.example.shikiflow.presentation.screen.main.details.common.comment.CommentsScreen
import com.example.shikiflow.presentation.screen.main.details.common.ExternalLinksScreen
import com.example.shikiflow.presentation.screen.main.details.common.ThreadsScreen
import com.example.shikiflow.presentation.screen.main.details.manga.MangaDetailsScreen
import com.example.shikiflow.presentation.screen.main.details.manga.read.MangaReadNavigator
import com.example.shikiflow.presentation.screen.main.details.staff.StaffScreen

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun DetailsNavigator(
    currentUserData: User?,
    authType: AuthType,
    mediaId: Int,
    mediaType: MediaType,
    source: String,
) {
    val twoPaneSceneStrategy = rememberListDetailSceneStrategy<NavKey>(
        backNavigationBehavior = BackNavigationBehavior.PopUntilContentChange
    )
    //val sceneStrategy = rememberTwoPaneSceneStrategy<NavKey>()
    val detailsBackstack = rememberNavBackStack(when(mediaType) {
        MediaType.ANIME -> DetailsNavRoute.AnimeDetails(mediaId, authType)
        MediaType.MANGA -> DetailsNavRoute.MangaDetails(mediaId, authType)
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

        override fun navigateToCharacterDetails(characterId: Int) {
            twoPaneNavigate(DetailsNavRoute.CharacterDetails(characterId))
        }

        override fun navigateToMediaCharacters(
            mediaId: Int,
            mediaTitle: String,
            mediaType: MediaType
        ) {
            twoPaneNavigate(DetailsNavRoute.MediaCharacters(mediaId, mediaTitle, mediaType))
        }

        override fun navigateToAnimeDetails(animeId: Int) {
            twoPaneNavigate(DetailsNavRoute.AnimeDetails(animeId, authType))
        }

        override fun navigateToMangaDetails(mangaId: Int) {
            twoPaneNavigate(DetailsNavRoute.MangaDetails(mangaId, authType))
        }

        override fun navigateToSimilarPage(id: Int, title: String, mediaType: MediaType) {
            twoPaneNavigate(DetailsNavRoute.SimilarPage(id, title, mediaType))
        }

        override fun navigateToLinksPage(id: Int, mediaType: MediaType) {
            twoPaneNavigate(DetailsNavRoute.ExternalLinks(mediaId, mediaType))
        }

        override fun navigateToMangaRead(mangaDexIds: List<String>, title: String, completedChapters: Int) {
            twoPaneNavigate(DetailsNavRoute.MangaRead(mangaDexIds, title, completedChapters))
        }

        override fun navigateToThreads(mediaId: Int) {
            twoPaneNavigate(DetailsNavRoute.Threads(mediaId))
        }

        override fun navigateToComments(
            screenMode: CommentsScreenMode,
            id: Int,
            threadHeader: Thread?
        ) {
            twoPaneNavigate(DetailsNavRoute.Comments(screenMode, authType, id, threadHeader))
        }

        override fun navigateToStaff(personId: Int) {
            twoPaneNavigate(DetailsNavRoute.Staff(personId))
        }

        override fun navigateToAnimeWatch(title: String, shikimoriId: Int, completedEpisodes: Int) {
            twoPaneNavigate(DetailsNavRoute.AnimeWatch(title, shikimoriId, completedEpisodes))
        }

        override fun navigateToStudio(id: Int, studioName: String) {
            twoPaneNavigate(DetailsNavRoute.Studio(id, studioName, authType))
        }

        override fun navigateByEntity(entityType: EntityType, id: Int) {
            when (entityType) {
                EntityType.CHARACTER -> {
                    navigateToCharacterDetails(id)
                }
                EntityType.PERSON -> {
                    navigateToStaff(id)
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
                    authType = route.authType,
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
                    authType = route.authType,
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
            entry<DetailsNavRoute.Threads> { route ->
                ThreadsScreen(
                    mediaId = route.mediaId,
                    navOptions = options,
                    threadsViewModel = hiltViewModel(key = source)
                )
            }
            entry<DetailsNavRoute.Comments> { route ->
                CommentsScreen(
                    threadHeader = route.threadHeader,
                    screenMode = route.screenMode,
                    authType = route.authType,
                    id = route.id,
                    navOptions = options,
                    commentViewModel = hiltViewModel(key = source)
                )
            }
            entry<DetailsNavRoute.Staff> { route ->
                StaffScreen(
                    personId = route.staffId,
                    navOptions = options,
                    staffViewModel = hiltViewModel(key = source)
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
                    authType = route.authType,
                    onNavigateBack = { options.navigateBack() },
                    onMediaNavigate = { animeId ->
                        options.navigateToAnimeDetails(animeId)
                    },
                    studioViewModel = hiltViewModel(key = source)
                )
            }
            entry<DetailsNavRoute.MediaCharacters> { route ->
                MediaCharactersScreen(
                    mediaId = route.mediaId,
                    mediaTitle = route.mediaTitle,
                    mediaType = route.mediaType,
                    navOptions = options
                )
            }
        },
        transitionSpec = {
            slideInHorizontally(
                initialOffsetX = { it },
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioNoBouncy,
                    stiffness = Spring.StiffnessMediumLow
                )
            ) togetherWith ExitTransition.KeepUntilTransitionsFinished
        },
        popTransitionSpec = {
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
        },
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator()
        )
    )
}