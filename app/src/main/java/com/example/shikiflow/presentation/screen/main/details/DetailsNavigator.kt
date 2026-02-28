package com.example.shikiflow.presentation.screen.main.details

import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
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

@Composable
fun DetailsNavigator(
    currentUserData: User?,
    authType: AuthType,
    mediaId: Int,
    mediaType: MediaType,
    source: String,
) {
    val detailsBackstack = rememberNavBackStack(when(mediaType) {
        MediaType.ANIME -> DetailsNavRoute.AnimeDetails(mediaId, authType)
        MediaType.MANGA -> DetailsNavRoute.MangaDetails(mediaId, authType)
    })

    val options = object : MediaNavOptions {
        //Removed two pane scene strategy due to the changes in the Material Adaptive API
        override fun navigateToCharacterDetails(characterId: Int) {
            detailsBackstack.add(DetailsNavRoute.CharacterDetails(characterId))
        }

        override fun navigateToMediaCharacters(
            mediaId: Int,
            mediaTitle: String,
            mediaType: MediaType
        ) {
            detailsBackstack.add(DetailsNavRoute.MediaCharacters(mediaId, mediaTitle, mediaType))
        }

        override fun navigateToAnimeDetails(animeId: Int) {
            detailsBackstack.add(DetailsNavRoute.AnimeDetails(animeId, authType))
        }

        override fun navigateToMangaDetails(mangaId: Int) {
            detailsBackstack.add(DetailsNavRoute.MangaDetails(mangaId, authType))
        }

        override fun navigateToSimilarPage(id: Int, title: String, mediaType: MediaType) {
            detailsBackstack.add(DetailsNavRoute.SimilarPage(id, title, mediaType))
        }

        override fun navigateToLinksPage(id: Int, mediaType: MediaType) {
            detailsBackstack.add(DetailsNavRoute.ExternalLinks(mediaId, mediaType))
        }

        override fun navigateToMangaRead(mangaDexIds: List<String>, title: String, completedChapters: Int) {
            detailsBackstack.add(DetailsNavRoute.MangaRead(mangaDexIds, title, completedChapters))
        }

        override fun navigateToThreads(mediaId: Int) {
            detailsBackstack.add(DetailsNavRoute.Threads(mediaId))
        }

        override fun navigateToComments(
            screenMode: CommentsScreenMode,
            id: Int,
            threadHeader: Thread?
        ) {
            detailsBackstack.add(DetailsNavRoute.Comments(screenMode, authType, id, threadHeader))
        }

        override fun navigateToStaff(personId: Int) {
            detailsBackstack.add(DetailsNavRoute.Staff(personId))
        }

        override fun navigateToAnimeWatch(title: String, shikimoriId: Int, completedEpisodes: Int) {
            detailsBackstack.add(DetailsNavRoute.AnimeWatch(title, shikimoriId, completedEpisodes))
        }

        override fun navigateToStudio(id: Int, studioName: String) {
            detailsBackstack.add(DetailsNavRoute.Studio(id, studioName, authType))
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
        entryProvider = entryProvider {
            entry<DetailsNavRoute.AnimeDetails> { route ->
                AnimeDetailsScreen(
                    id = route.id,
                    authType = route.authType,
                    userId = currentUserData?.id,
                    navOptions = options,
                    animeDetailsViewModel = hiltViewModel(key = source)
                )
            }
            entry<DetailsNavRoute.MangaDetails> { route ->
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
                    authType = authType,
                    navOptions = options,
                    characterDetailsViewModel = hiltViewModel(key = source)
                )
            }
            entry<DetailsNavRoute.SimilarPage> { route ->
                SimilarMediaScreen(
                    mediaTitle = route.title,
                    mediaId = route.id,
                    mediaType = route.mediaType,
                    navOptions = options,
                    similarMediaViewModel = hiltViewModel(key = source)
                )
            }
            entry<DetailsNavRoute.ExternalLinks> { route ->
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
        },
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator()
        )
    )
}