package com.example.shikiflow.presentation.screen.more

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.example.shikiflow.domain.model.auth.AuthType
import com.example.shikiflow.domain.model.user.User
import com.example.shikiflow.presentation.screen.main.details.DetailsNavRoute
import com.example.shikiflow.presentation.screen.main.details.DetailsNavigator
import com.example.shikiflow.presentation.screen.more.about.AboutAppScreen
import com.example.shikiflow.presentation.screen.more.history.UserActivityScreen
import com.example.shikiflow.presentation.screen.more.profile.ProfileNavigator
import com.example.shikiflow.presentation.screen.more.settings.SettingsScreen

@Composable
fun MoreScreenNavigator(
    moreScreenBackStack: NavBackStack<NavKey>,
    currentUser: User?,
    authType: AuthType
) {
    //val moreBackstack = rememberNavBackStack(MoreNavRoute.MoreScreen)

    val moreNavOptions = object : MoreNavOptions {
        override fun navigateToProfile(user: User?) {
            moreScreenBackStack.add(MoreNavRoute.Profile(user))
        }
        override fun navigateToHistory() {
            moreScreenBackStack.add(MoreNavRoute.UserActivity)
        }
        override fun navigateToSettings() {
            moreScreenBackStack.add(MoreNavRoute.Settings)
        }
        override fun navigateToAbout() {
            moreScreenBackStack.add(MoreNavRoute.AboutApp)
        }

        override fun navigateToDetails(detailsNavRoute: DetailsNavRoute) {
            moreScreenBackStack.add(MoreNavRoute.Details(detailsNavRoute))
        }

        override fun navigateBack() {
            moreScreenBackStack.removeLastOrNull()
        }
    }

    NavDisplay(
        backStack = moreScreenBackStack,
        onBack = { moreScreenBackStack.removeLastOrNull() },
        entryProvider = entryProvider {
            entry<MoreNavRoute.MoreMain> {
                MoreScreen(
                    moreNavOptions = moreNavOptions,
                    currentUser = currentUser
                )
            }
            entry<MoreNavRoute.Profile> { route ->
                ProfileNavigator(
                    user = route.user,
                    mainNavOptions = moreNavOptions
                )
            }
            entry<MoreNavRoute.UserActivity> {
                UserActivityScreen(
                    profileNavOptions = moreNavOptions
                )
            }
            entry<MoreNavRoute.Settings> {
                SettingsScreen(
                    userData = currentUser
                )
            }
            entry<MoreNavRoute.AboutApp> {
                AboutAppScreen()
            }
            entry<MoreNavRoute.Details> { route ->
                DetailsNavigator(
                    currentUserData = currentUser,
                    authType = authType,
                    detailsNavRoute = route.detailsNavRoute,
                    navOptions = moreNavOptions
                )
            }
        },
        transitionSpec = {
            fadeIn(
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioNoBouncy,
                    stiffness = Spring.StiffnessMediumLow
                )
            ) togetherWith ExitTransition.None
        },
        popTransitionSpec = {
            EnterTransition.None togetherWith fadeOut(
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