package com.example.shikiflow.presentation.screen.more

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.example.shikiflow.domain.model.user.User
import com.example.shikiflow.presentation.screen.more.about.AboutAppScreen
import com.example.shikiflow.presentation.screen.more.compare.CompareScreen
import com.example.shikiflow.presentation.screen.more.history.HistoryScreen
import com.example.shikiflow.presentation.screen.more.profile.ProfileScreen
import com.example.shikiflow.presentation.screen.more.settings.SettingsScreen

@Composable
fun MoreScreenNavigator(
    currentUser: User?
) {
    val moreBackstack = rememberNavBackStack(MoreNavRoute.MoreScreen)

    val moreNavOptions = object : MoreNavOptions {
        override fun navigateToProfile(user: User?) {
            moreBackstack.add(MoreNavRoute.ProfileScreen(user))
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

        override fun navigateToCompare(targetUser: User) {
            moreBackstack.add(MoreNavRoute.CompareScreen(targetUser))
        }

        override fun navigateBack() {
            moreBackstack.removeLastOrNull()
        }
    }

    NavDisplay(
        backStack = moreBackstack,
        onBack = { moreBackstack.removeLastOrNull() },
        entryProvider = entryProvider {
            currentUser?.let {
                entry<MoreNavRoute.MoreScreen> {
                    MoreScreen(
                        moreNavOptions = moreNavOptions,
                        currentUser = currentUser
                    )
                }
                entry<MoreNavRoute.ProfileScreen> { route ->
                    ProfileScreen(
                        currentUserId = currentUser.id,
                        userData = route.user,
                        moreNavOptions = moreNavOptions
                    )
                }
                entry<MoreNavRoute.HistoryScreen> {
                    HistoryScreen(
                        currentUserId = currentUser.id,
                        moreNavOptions = moreNavOptions,
                    )
                }
                entry<MoreNavRoute.SettingsScreen> {
                    SettingsScreen(
                        userData = currentUser
                    )
                }
                entry<MoreNavRoute.CompareScreen>(
                    metadata = NavDisplay.transitionSpec {
                        slideInHorizontally(
                            initialOffsetX = { it },
                            animationSpec = tween(300)
                        ) togetherWith ExitTransition.KeepUntilTransitionsFinished
                    } + NavDisplay.popTransitionSpec {
                        EnterTransition.None togetherWith slideOutHorizontally(
                            targetOffsetX = { it },
                            animationSpec = tween(300)
                        )
                    } + NavDisplay.predictivePopTransitionSpec {
                        EnterTransition.None togetherWith slideOutHorizontally(
                            targetOffsetX = { it },
                            animationSpec = tween(300)
                        )
                    }
                ) { route ->
                    CompareScreen(
                        currentUser = currentUser,
                        targetUser = route.targetUser,
                        moreNavOptions = moreNavOptions
                    )
                }
                entry<MoreNavRoute.AboutAppScreen> {
                    AboutAppScreen()
                }
            }
        },
        transitionSpec = {
            fadeIn(
                animationSpec = tween(300)
            ) togetherWith ExitTransition.KeepUntilTransitionsFinished
        },
        popTransitionSpec = {
            EnterTransition.None togetherWith fadeOut(
                animationSpec = tween(300)
            )
        },
        predictivePopTransitionSpec = {
            EnterTransition.None togetherWith fadeOut(
                animationSpec = tween(300)
            )
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