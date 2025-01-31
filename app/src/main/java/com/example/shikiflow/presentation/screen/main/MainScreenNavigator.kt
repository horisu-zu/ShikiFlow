package com.example.shikiflow.presentation.screen.main

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.graphql.CurrentUserQuery
import com.example.shikiflow.presentation.screen.main.details.AnimeDetailsScreen
import com.example.shikiflow.utils.Animations.slideInFromLeft
import com.example.shikiflow.utils.Animations.slideInFromRight
import com.example.shikiflow.utils.Animations.slideOutToLeft
import com.example.shikiflow.utils.Animations.slideOutToRight

@Composable
fun MainScreenNavigator(
    currentUser: CurrentUserQuery.Data?,
) {
    val mainNavController = rememberNavController()

    NavHost(
        startDestination = "mainScreen",
        navController = mainNavController
    ) {
        composable (
            route = "mainScreen",
            enterTransition = { slideInFromRight() },
            exitTransition = { slideOutToLeft() },
            popEnterTransition = { slideInFromLeft() },
            popExitTransition = { slideOutToRight() }
        ) {
            MainScreen(
                mainNavController = mainNavController,
                currentUser = currentUser
            )
        }
        composable(
            route = "animeDetailsScreen/{id}",
            enterTransition = { slideInFromRight() },
            exitTransition = { slideOutToLeft() },
            popEnterTransition = { slideInFromLeft() },
            popExitTransition = { slideOutToRight() }
        ) {
            AnimeDetailsScreen(
                id = (it.arguments?.getString("id") ?: 0).toString(),
            )
        }
    }
}