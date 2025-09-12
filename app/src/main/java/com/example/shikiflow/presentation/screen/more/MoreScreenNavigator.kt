package com.example.shikiflow.presentation.screen.more

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.entry
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.example.graphql.CurrentUserQuery
import com.example.shikiflow.presentation.screen.more.about.AboutAppScreen
import com.example.shikiflow.presentation.screen.more.history.HistoryScreen
import com.example.shikiflow.presentation.screen.more.profile.ProfileScreen
import com.example.shikiflow.presentation.screen.more.settings.SettingsScreen
import com.example.shikiflow.presentation.viewmodel.AboutViewModel

@Composable
fun MoreScreenNavigator(
    currentUser: CurrentUserQuery.Data?
) {
    val moreBackstack = rememberNavBackStack(MoreNavRoute.MoreScreen)
    //val moreNavController = rememberNavController()
    val aboutViewModel = hiltViewModel<AboutViewModel>()

    val moreNavOptions = object : MoreNavOptions {
        override fun navigateToProfile() {
            moreBackstack.add(MoreNavRoute.ProfileScreen)
        }
        override fun navigateToHistory() {
            moreBackstack.add(MoreNavRoute.HistoryScreen)
        }
        override fun navigateToSettings() {
            moreBackstack.add(MoreNavRoute.SettingsScreen)
        }
        override fun navigateToAbout() {
            moreBackstack.add(MoreNavRoute.AboutAppScreen)
        }
        override fun navigateBack() {
            moreBackstack.removeLastOrNull()
        }
    }

    NavDisplay(
        backStack = moreBackstack,
        onBack = { moreBackstack.removeLastOrNull() },
        entryProvider = entryProvider {
            entry<MoreNavRoute.MoreScreen> {
                MoreScreen(
                    moreNavOptions = moreNavOptions,
                    currentUser = currentUser
                )
            }
            entry<MoreNavRoute.ProfileScreen> {
                ProfileScreen(
                    moreNavOptions = moreNavOptions,
                    currentUser = currentUser
                )
            }
            entry<MoreNavRoute.HistoryScreen> {
                HistoryScreen(
                    userData = currentUser,
                    moreNavOptions = moreNavOptions,
                )
            }
            entry<MoreNavRoute.SettingsScreen> {
                SettingsScreen(
                    userData = currentUser
                )
            }
            entry<MoreNavRoute.AboutAppScreen> {
                AboutAppScreen(aboutViewModel)
            }
        }
    )

    /*NavHost(
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
    }*/
}