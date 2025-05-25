package com.example.shikiflow.presentation.screen

import androidx.annotation.DrawableRes
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entry
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.example.graphql.CurrentUserQuery
import com.example.shikiflow.R
import com.example.shikiflow.presentation.screen.browse.BrowseScreenNavigator
import com.example.shikiflow.presentation.screen.main.MainScreen
import com.example.shikiflow.presentation.screen.main.details.anime.AnimeDetailsScreen
import com.example.shikiflow.presentation.screen.main.details.character.CharacterDetailsScreen
import com.example.shikiflow.presentation.screen.main.details.manga.MangaDetailsScreen
import com.example.shikiflow.presentation.screen.more.MoreScreenNavigator
import com.example.shikiflow.utils.AppSettingsManager

@Composable
fun BottomNavigationBar(
    currentRoute: NavKey,
    onNavigate: (MainNavRoute) -> Unit
) {
    val items = listOf(
        BottomNavItem.Home,
        BottomNavItem.Browse,
        BottomNavItem.More
    )

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        items.forEach { item ->
            val isSelected = when (currentRoute) {
                MainNavRoute.Home -> item.route == MainNavRoute.Home
                MainNavRoute.Browse -> item.route == MainNavRoute.Browse
                MainNavRoute.More -> item.route == MainNavRoute.More
                else -> false
            }

            NavigationBarItem(
                icon = {
                    Icon(
                        painterResource(
                            id = if (isSelected) item.selectedIconRes else item.unselectedIconRes
                        ),
                        contentDescription = item.title,
                        modifier = Modifier.size(24.dp)
                    )
                },
                label = { Text(item.title) },
                selected = isSelected,
                onClick = {
                    if (!isSelected) {
                        onNavigate(item.route)
                    }
                }
            )
        }
    }
}

@Composable
fun NavigationGraph(
    appSettingsManager: AppSettingsManager,
    mainBackstack: NavBackStack,
    modifier: Modifier = Modifier,
    currentUser: CurrentUserQuery.Data?
) {
    //val mainBackstack = remember { mutableStateListOf<MainNavRoute>(MainNavRoute.Home) }
    val options = object : MediaNavOptions {
        override fun navigateToCharacterDetails(characterId: String) {
            mainBackstack.add(MainNavRoute.CharacterDetails(characterId))
        }

        override fun navigateToAnimeDetails(animeId: String) {
            mainBackstack.add(MainNavRoute.AnimeDetails(animeId))
        }

        override fun navigateToMangaDetails(mangaId: String) {
            mainBackstack.add(MainNavRoute.MangaDetails(mangaId))
        }

        override fun navigateBack() {
            mainBackstack.removeLastOrNull()
        }
    }

    NavDisplay(
        backStack = mainBackstack,
        onBack = { mainBackstack.removeLastOrNull() },
        modifier = modifier,
        entryProvider = entryProvider {
            entry<MainNavRoute.Home> {
                MainScreen(
                    appSettingsManager = appSettingsManager,
                    currentUser = currentUser,
                    onAnimeClick = { animeId ->
                        mainBackstack.add(MainNavRoute.AnimeDetails(animeId))
                    },
                    onMangaClick = { mangaId ->
                        mainBackstack.add(MainNavRoute.MangaDetails(mangaId))
                    }
                )
            }
            entry<MainNavRoute.Browse> {
                BrowseScreenNavigator(navOptions = options)
            }
            entry<MainNavRoute.More> {
                MoreScreenNavigator(currentUser = currentUser)
            }
            entry<MainNavRoute.AnimeDetails>(
                metadata = NavDisplay.transitionSpec {
                    slideInHorizontally(
                        initialOffsetX = { it },
                        animationSpec = tween(300)
                    ) togetherWith ExitTransition.KeepUntilTransitionsFinished
                }
            ) { route ->
                AnimeDetailsScreen(
                    id = route.id,
                    currentUser = currentUser,
                    navOptions = options
                )
            }
            entry<MainNavRoute.MangaDetails>(
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
                    currentUser = currentUser
                )
            }
            entry<MainNavRoute.CharacterDetails>(
                metadata = NavDisplay.transitionSpec {
                    slideInHorizontally(
                        initialOffsetX = { it },
                        animationSpec = tween(300)
                    ) togetherWith ExitTransition.KeepUntilTransitionsFinished
                }
            ) { route ->
                CharacterDetailsScreen(
                    characterId = route.characterId,
                    navOptions = options
                )
            }
        }
    )

    /*NavHost(navController, startDestination = MainNavRoute.Home, modifier = modifier) {
        composable<MainNavRoute.Home> {
            MainScreen(
                appSettingsManager = appSettingsManager,
                currentUser = currentUser,
                onAnimeClick = { animeId ->
                    navController.navigate(MainNavRoute.AnimeDetails(animeId))
                },
                onMangaClick = { mangaId ->
                    navController.navigate(MainNavRoute.MangaDetails(mangaId))
                }
            )
        }
        composable<MainNavRoute.Browse> {
            BrowseScreenNavigator(
                navOptions = options
            )
        }
        composable<MainNavRoute.More> {
            MoreScreenNavigator(currentUser = currentUser)
        }
        composable<MainNavRoute.AnimeDetails>(
            enterTransition = { slideInFromRight() },
            exitTransition = { slideOutToLeft() },
            popEnterTransition = { slideInFromLeft() },
            popExitTransition = { slideOutToRight() }
        ) { backStackEntry ->
            val args = backStackEntry.toRoute<MainNavRoute.AnimeDetails>()
            AnimeDetailsScreen(
                id = args.id,
                currentUser = currentUser,
                navOptions = options
            )
        }
        composable<MainNavRoute.MangaDetails>(
            enterTransition = { slideInFromRight() },
            exitTransition = { slideOutToLeft() },
            popEnterTransition = { slideInFromLeft() },
            popExitTransition = { slideOutToRight() }
        ) { backStackEntry ->
            val args = backStackEntry.toRoute<MainNavRoute.MangaDetails>()
            MangaDetailsScreen(
                id = args.id,
                navOptions = options,
                currentUser = currentUser
            )
        }
        composable<MainNavRoute.CharacterDetails>(
            enterTransition = { slideInFromRight() },
            exitTransition = { slideOutToLeft() },
            popEnterTransition = { slideInFromLeft() },
            popExitTransition = { slideOutToRight() }
        ) { backStackEntry ->
            val args = backStackEntry.toRoute<MainNavRoute.CharacterDetails>()
            CharacterDetailsScreen(
                characterId = args.characterId,
                navOptions = options
            )
        }
    }*/
}

sealed class BottomNavItem(
    var title: String,
    @DrawableRes var selectedIconRes: Int,
    @DrawableRes var unselectedIconRes: Int,
    var route: MainNavRoute
) {
    object Home : BottomNavItem(
        "Main",
        R.drawable.ic_selected_book,
        R.drawable.ic_unselected_book,
        MainNavRoute.Home
    )

    object Browse : BottomNavItem(
        "Browse",
        R.drawable.ic_selected_browse,
        R.drawable.ic_unselected_browse,
        MainNavRoute.Browse
    )

    object More : BottomNavItem(
        "More",
        R.drawable.ic_selected_dots,
        R.drawable.ic_unselected_dots,
        MainNavRoute.More
    )
}