package com.example.shikiflow.presentation.screen

import androidx.annotation.DrawableRes
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
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.toRoute
import com.example.graphql.CurrentUserQuery
import com.example.shikiflow.R
import com.example.shikiflow.presentation.screen.browse.BrowseScreenNavigator
import com.example.shikiflow.presentation.screen.main.MainScreen
import com.example.shikiflow.presentation.screen.main.details.anime.AnimeDetailsScreen
import com.example.shikiflow.presentation.screen.main.details.character.CharacterDetailsScreen
import com.example.shikiflow.presentation.screen.main.details.manga.MangaDetailsScreen
import com.example.shikiflow.presentation.screen.more.MoreScreenNavigator
import com.example.shikiflow.utils.Animations.slideInFromLeft
import com.example.shikiflow.utils.Animations.slideInFromRight
import com.example.shikiflow.utils.Animations.slideOutToLeft
import com.example.shikiflow.utils.Animations.slideOutToRight

@Composable
fun BottomNavigationBar(navController: NavHostController) {
    val items = listOf(
        BottomNavItem.Home,
        BottomNavItem.Browse,
        BottomNavItem.More
    )

    val navBackStackEntry = navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry.value?.destination?.route

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        items.forEach { item ->
            val isSelected = currentRoute?.contains(item.route.toString()) == true

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
                        navController.navigate(item.route) {
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                }
            )
        }
    }
}

@Composable
fun NavigationGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    currentUser: CurrentUserQuery.Data?
) {
    val options = object : MediaNavOptions {
        override fun navigateToCharacterDetails(characterId: String) {
            navController.navigate(MainNavRoute.CharacterDetails(characterId))
        }

        override fun navigateToAnimeDetails(animeId: String) {
            navController.navigate(MainNavRoute.AnimeDetails(animeId))
        }

        override fun navigateToMangaDetails(mangaId: String) {
            navController.navigate(MainNavRoute.MangaDetails(mangaId))
        }

        override fun navigateBack() {
            navController.popBackStack()
        }
    }

    NavHost(navController, startDestination = MainNavRoute.Home, modifier = modifier) {
        composable<MainNavRoute.Home> {
            MainScreen(
                currentUser = currentUser,
                onAnimeClick = { animeId ->
                    navController.navigate(MainNavRoute.AnimeDetails(animeId))
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
    }
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