@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.shikiflow.presentation.screen.main.details

import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.example.shikiflow.domain.model.comment.CommentsScreenMode
import com.example.shikiflow.domain.model.comment.EntityType
import com.example.shikiflow.domain.model.tracks.MediaType
import com.example.shikiflow.domain.model.user.User
import com.example.shikiflow.presentation.common.scene.BottomSheetSceneStrategy
import com.example.shikiflow.presentation.screen.MainNavOptions
import com.example.shikiflow.presentation.screen.main.details.common.SimilarMediaScreen
import com.example.shikiflow.presentation.screen.main.details.anime.AnimeDetailsScreen
import com.example.shikiflow.presentation.screen.main.details.anime.studio.StudioScreen
import com.example.shikiflow.presentation.screen.main.details.anime.watch.AnimeWatchNavigator
import com.example.shikiflow.presentation.screen.main.details.character.CharacterDetailsScreen
import com.example.shikiflow.presentation.screen.main.details.roles.MediaRolesScreen
import com.example.shikiflow.presentation.screen.main.details.character.MediaCharactersScreen
import com.example.shikiflow.presentation.screen.main.details.common.comment.CommentsScreen
import com.example.shikiflow.presentation.screen.main.details.common.ExternalLinksScreen
import com.example.shikiflow.presentation.screen.main.details.common.ThreadsScreen
import com.example.shikiflow.presentation.screen.main.details.common.followings.MediaFollowingsScreen
import com.example.shikiflow.presentation.screen.main.details.common.review.MediaReviewsScreen
import com.example.shikiflow.presentation.screen.main.details.common.review.ReviewScreen
import com.example.shikiflow.presentation.screen.main.details.manga.MangaDetailsScreen
import com.example.shikiflow.presentation.screen.main.details.manga.read.MangaReadNavigator
import com.example.shikiflow.presentation.screen.main.details.staff.MediaStaffScreen
import com.example.shikiflow.presentation.screen.main.details.staff.StaffScreen

@Composable
fun DetailsNavigator(
    detailsNavRoute: DetailsNavRoute,
    mainNavOptions: MainNavOptions
) {
    val detailsBackstack = rememberNavBackStack(detailsNavRoute)
    val bottomSheetStrategy = remember { BottomSheetSceneStrategy<NavKey>() }

    val navOptions = object : MediaNavOptions {
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
            detailsBackstack.add(DetailsNavRoute.AnimeDetails(animeId))
        }

        override fun navigateToMangaDetails(mangaId: Int) {
            detailsBackstack.add(DetailsNavRoute.MangaDetails(mangaId))
        }

        override fun navigateToSimilarPage(id: Int, title: String, mediaType: MediaType) {
            detailsBackstack.add(DetailsNavRoute.SimilarMedia(id, title, mediaType))
        }

        override fun navigateToLinksPage(id: Int, mediaType: MediaType) {
            detailsBackstack.add(DetailsNavRoute.ExternalLinks(id, mediaType))
        }

        override fun navigateToMangaRead(
            mangaDexIds: List<String>,
            trackerMangaId: Int,
            malId: Int?,
            title: String,
            completedChapters: Int
        ) {
            detailsBackstack.add(DetailsNavRoute.MangaRead(mangaDexIds, trackerMangaId, malId, title, completedChapters))
        }

        override fun navigateToThreads(mediaId: Int) {
            detailsBackstack.add(DetailsNavRoute.Threads(mediaId))
        }

        override fun navigateToComments(screenMode: CommentsScreenMode, id: Int) {
            detailsBackstack.add(DetailsNavRoute.Comments(screenMode, id))
        }

        override fun navigateToStaff(staffId: Int) {
            detailsBackstack.add(DetailsNavRoute.Staff(staffId))
        }

        override fun navigateToMediaStaff(
            mediaId: Int,
            mediaType: MediaType
        ) {
            detailsBackstack.add(DetailsNavRoute.MediaStaff(mediaId, mediaType))
        }

        override fun navigateToAnimeWatch(title: String, shikimoriId: Int, completedEpisodes: Int) {
            detailsBackstack.add(DetailsNavRoute.AnimeWatch(title, shikimoriId, completedEpisodes))
        }

        override fun navigateToStudio(id: Int, studioName: String) {
            detailsBackstack.add(DetailsNavRoute.Studio(id, studioName))
        }

        override fun navigateToMediaRoles(
            id: Int,
            mediaRolesType: MediaRolesType,
            roleTypes: List<RoleType>
        ) {
            detailsBackstack.add(DetailsNavRoute.MediaRoles(id, mediaRolesType, roleTypes))
        }

        override fun navigateToMediaReviews(
            id: Int,
            mediaType: MediaType
        ) {
            detailsBackstack.add(DetailsNavRoute.MediaReviews(id, mediaType))
        }

        override fun navigateToReview(id: Int) {
            detailsBackstack.add(DetailsNavRoute.Review(id))
        }

        override fun navigateToMediaFollowings(mediaId: Int, totalCount: Int?) {
            detailsBackstack.add(DetailsNavRoute.MediaFollowings(mediaId, totalCount))
        }

        override fun navigateToUserProfile(user: User) {
            mainNavOptions.navigateToProfile(user)
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
                EntityType.COMMENT_REPLY -> {
                    navigateToComments(
                        screenMode = CommentsScreenMode.REPLY,
                        id = id
                    )
                }
                EntityType.REVIEW -> {
                    navigateToReview(id)
                }
            }
        }

        override fun navigateBack() {
            if(detailsBackstack.size > 1) {
                detailsBackstack.removeLastOrNull()
            } else mainNavOptions.navigateBack()
        }
    }

    NavDisplay(
        backStack = detailsBackstack,
        sceneStrategies = listOf(bottomSheetStrategy),
        onBack = { navOptions.navigateBack() },
        entryProvider = entryProvider {
            entry<DetailsNavRoute.AnimeDetails> { route ->
                AnimeDetailsScreen(
                    id = route.id,
                    navOptions = navOptions
                )
            }
            entry<DetailsNavRoute.MangaDetails> { route ->
                MangaDetailsScreen(
                    id = route.id,
                    navOptions = navOptions
                )
            }
            entry<DetailsNavRoute.CharacterDetails> { route ->
                CharacterDetailsScreen(
                    characterId = route.characterId,
                    navOptions = navOptions
                )
            }
            entry<DetailsNavRoute.SimilarMedia>(
                metadata = BottomSheetSceneStrategy.bottomSheet()
            ) { route ->
                SimilarMediaScreen(
                    mediaTitle = route.title,
                    mediaId = route.id,
                    mediaType = route.mediaType,
                    onMediaNavigate = { id, mediaType ->
                        navOptions.navigateBack().let {
                            when (mediaType) {
                                MediaType.ANIME -> navOptions.navigateToAnimeDetails(id)
                                MediaType.MANGA -> navOptions.navigateToMangaDetails(id)
                            }
                        }
                    }
                )
            }
            entry<DetailsNavRoute.ExternalLinks>(
                metadata = BottomSheetSceneStrategy.bottomSheet()
            ) { route ->
                ExternalLinksScreen(
                    mediaId = route.mediaId,
                    mediaType = route.mediaType
                )
            }
            entry<DetailsNavRoute.MangaRead> { route ->
                MangaReadNavigator(
                    mangaDexIds = route.mangaDexIds,
                    mangaId = route.trackerMangaId,
                    malId = route.malId,
                    title = route.title,
                    onNavigateBack = { navOptions.navigateBack() }
                )
            }
            entry<DetailsNavRoute.Threads> { route ->
                ThreadsScreen(
                    mediaId = route.mediaId,
                    navOptions = navOptions
                )
            }
            entry<DetailsNavRoute.Comments> { route ->
                CommentsScreen(
                    screenMode = route.screenMode,
                    id = route.id,
                    navOptions = navOptions
                )
            }
            entry<DetailsNavRoute.Staff> { route ->
                StaffScreen(
                    staffId = route.staffId,
                    navOptions = navOptions
                )
            }
            entry<DetailsNavRoute.MediaStaff> { route ->
                MediaStaffScreen(
                    mediaId = route.mediaId,
                    mediaType = route.mediaType,
                    navOptions = navOptions
                )
            }
            entry<DetailsNavRoute.AnimeWatch> { route ->
                AnimeWatchNavigator(
                    title = route.title,
                    shikimoriId = route.shikimoriId,
                    completedEpisodes = route.completedEpisodes,
                    onNavigateBack = { navOptions.navigateBack() }
                )
            }
            entry<DetailsNavRoute.Studio> { route ->
                StudioScreen(
                    id = route.id,
                    studioName = route.studioName,
                    onNavigateBack = { navOptions.navigateBack() },
                    onMediaNavigate = { animeId ->
                        navOptions.navigateToAnimeDetails(animeId)
                    }
                )
            }
            entry<DetailsNavRoute.MediaCharacters> { route ->
                MediaCharactersScreen(
                    mediaId = route.mediaId,
                    mediaTitle = route.mediaTitle,
                    mediaType = route.mediaType,
                    navOptions = navOptions
                )
            }
            entry<DetailsNavRoute.MediaRoles> { route ->
                MediaRolesScreen(
                    id = route.id,
                    mediaRolesType = route.mediaRolesType,
                    roleTypes = route.roleTypes,
                    navOptions = navOptions
                )
            }
            entry<DetailsNavRoute.MediaReviews> { route ->
                MediaReviewsScreen(
                    mediaId = route.mediaId,
                    mediaType = route.mediaType,
                    navOptions = navOptions
                )
            }
            entry<DetailsNavRoute.Review> { route ->
                ReviewScreen(
                    reviewId = route.id,
                    navOptions = navOptions
                )
            }
            entry<DetailsNavRoute.MediaFollowings> { route ->
                MediaFollowingsScreen(
                    mediaId = route.mediaId,
                    totalCount = route.totalCount,
                    navOptions = navOptions
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