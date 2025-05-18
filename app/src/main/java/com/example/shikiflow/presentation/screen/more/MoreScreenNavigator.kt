package com.example.shikiflow.presentation.screen.more

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.graphql.CurrentUserQuery
import com.example.shikiflow.presentation.screen.more.about.AboutAppScreen
import com.example.shikiflow.presentation.screen.more.history.HistoryScreen
import com.example.shikiflow.presentation.screen.more.profile.ProfileScreen
import com.example.shikiflow.presentation.screen.more.settings.SettingsScreen
import com.example.shikiflow.presentation.viewmodel.AboutViewModel
import com.example.shikiflow.utils.Animations.slideInFromLeft
import com.example.shikiflow.utils.Animations.slideInFromRight
import com.example.shikiflow.utils.Animations.slideOutToLeft
import com.example.shikiflow.utils.Animations.slideOutToRight

@Composable
fun MoreScreenNavigator(
    currentUser: CurrentUserQuery.Data?
) {
    val moreNavController = rememberNavController()
    val aboutViewModel = hiltViewModel<AboutViewModel>()

    val moreNavOptions = object : MoreNavOptions {
        override fun navigateToProfile() {
            moreNavController.navigate(MoreNavRoute.ProfileScreen)
        }
        override fun navigateToHistory() {
            moreNavController.navigate(MoreNavRoute.HistoryScreen)
        }
        override fun navigateToSettings() {
            moreNavController.navigate(MoreNavRoute.SettingsScreen)
        }
        override fun navigateToAbout() {
            moreNavController.navigate(MoreNavRoute.AboutAppScreen)
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
        composable<MoreNavRoute.SettingsScreen>(
            enterTransition = { slideInFromRight() },
            exitTransition = { slideOutToLeft() },
            popEnterTransition = { slideInFromLeft() },
            popExitTransition = { slideOutToRight() }
        ) {
            SettingsScreen(
                userData = currentUser
            )
        }
        composable<MoreNavRoute.AboutAppScreen>(
            enterTransition = { slideInFromRight() },
            exitTransition = { slideOutToLeft() },
            popEnterTransition = { slideInFromLeft() },
            popExitTransition = { slideOutToRight() }
        ) {
            AboutAppScreen(aboutViewModel)
        }
    }
}