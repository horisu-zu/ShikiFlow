package com.example.shikiflow.presentation.screen.more

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.graphql.CurrentUserQuery
import com.example.shikiflow.presentation.screen.more.history.HistoryScreen
import com.example.shikiflow.presentation.screen.more.profile.ProfileScreen
import com.example.shikiflow.utils.Animations.slideInFromLeft
import com.example.shikiflow.utils.Animations.slideInFromRight
import com.example.shikiflow.utils.Animations.slideOutToLeft
import com.example.shikiflow.utils.Animations.slideOutToRight

@Composable
fun MoreScreenNavigator(
    currentUser: CurrentUserQuery.Data?
) {
    val moreNavController = rememberNavController()
    val moreNavOptions = object : MoreNavOptions {
        override fun navigateToProfile() {
            moreNavController.navigate(MoreNavRoute.ProfileScreen)
        }
        override fun navigateToHistory() {
            moreNavController.navigate(MoreNavRoute.HistoryScreen)
        }
        override fun navigateBack() {
            moreNavController.popBackStack()
        }
    }

    NavHost(
        navController = moreNavController,
        startDestination = MoreNavRoute.MoreScreen
    ) {
        composable<MoreNavRoute.MoreScreen>(
            enterTransition = { slideInFromRight() },
            exitTransition = { slideOutToLeft() },
            popEnterTransition = { slideInFromLeft() },
            popExitTransition = { slideOutToRight() }
        ) {
            MoreScreen(
                moreNavOptions = moreNavOptions,
                currentUser = currentUser
            )
        }
        composable<MoreNavRoute.ProfileScreen>(
            enterTransition = { slideInFromRight() },
            exitTransition = { slideOutToLeft() },
            popEnterTransition = { slideInFromLeft() },
            popExitTransition = { slideOutToRight() }
        ) {
            ProfileScreen(
                moreNavOptions = moreNavOptions,
                currentUser = currentUser
            )
        }
        composable<MoreNavRoute.HistoryScreen>(
            enterTransition = { slideInFromRight() },
            exitTransition = { slideOutToLeft() },
            popEnterTransition = { slideInFromLeft() },
            popExitTransition = { slideOutToRight() }
        ) {
            HistoryScreen(
                userData = currentUser,
                moreNavOptions = moreNavOptions,
            )
        }
    }
}