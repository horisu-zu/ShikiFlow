package com.example.shikiflow.presentation.screen.browse

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.graphql.CurrentUserQuery
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
    }
}