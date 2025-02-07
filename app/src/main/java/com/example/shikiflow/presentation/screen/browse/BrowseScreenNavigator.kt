package com.example.shikiflow.presentation.screen.browse

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.graphql.CurrentUserQuery
import com.example.shikiflow.data.anime.BrowseScreens
import com.example.shikiflow.data.anime.BrowseType
import com.example.shikiflow.utils.Animations.slideInFromLeft
import com.example.shikiflow.utils.Animations.slideInFromRight
import com.example.shikiflow.utils.Animations.slideOutToLeft
import com.example.shikiflow.utils.Animations.slideOutToRight

@Composable
fun BrowseScreenNavigator(
    currentUser: CurrentUserQuery.Data?,
    rootNavController: NavController
) {
    val browseNavController = rememberNavController()

    NavHost(
        startDestination = "browseScreen",
        navController = browseNavController
    ) {
        composable(
            route = "browseScreen",
            enterTransition = { slideInFromRight() },
            exitTransition = { slideOutToLeft() },
            popEnterTransition = { slideInFromLeft() },
            popExitTransition = { slideOutToRight() }
        ) {
            BrowseScreen(
                browseNavController = browseNavController,
                rootNavController = rootNavController
            )
        }
        composable(
            route = BrowseScreens.SideScreen.route,
            arguments = listOf(
                navArgument(BrowseScreens.SideScreen.ARG_BROWSE_TYPE) {
                    type = NavType.StringType
                }
            ),
            enterTransition = { slideInFromRight() },
            exitTransition = { slideOutToLeft() },
            popEnterTransition = { slideInFromLeft() },
            popExitTransition = { slideOutToRight() }
        ) { backStackEntry ->
            val browseTypeString = backStackEntry.arguments?.getString(BrowseScreens.SideScreen.ARG_BROWSE_TYPE)
                ?: return@composable

            val browseType = BrowseScreens.SideScreen.parseBrowseType(browseTypeString)
                ?: return@composable

            BrowseSideScreen(
                browseType = browseType,
                rootNavController = rootNavController,
                browseNavController = browseNavController
            )
        }
    }
}