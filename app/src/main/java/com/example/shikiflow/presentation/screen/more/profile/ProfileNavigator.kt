package com.example.shikiflow.presentation.screen.more.profile

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.example.shikiflow.domain.model.user.User
import com.example.shikiflow.presentation.screen.MainNavOptions
import com.example.shikiflow.presentation.screen.main.details.DetailsNavRoute
import com.example.shikiflow.presentation.screen.main.details.DetailsNavigator
import com.example.shikiflow.presentation.screen.more.about.AboutAppScreen
import com.example.shikiflow.presentation.screen.more.compare.CompareScreen
import com.example.shikiflow.presentation.screen.more.settings.SettingsScreen

@Composable
fun ProfileNavigator(
    user: User? = null,
    profileBackstack: NavBackStack<NavKey>? = null,
    mainNavOptions: MainNavOptions? = null
) {
    val backstack = profileBackstack ?: rememberNavBackStack(ProfileNavRoute.Profile(user))

    val profileNavOptions = object : ProfileNavOptions {
        override fun navigateToProfile(user: User?) {
            backstack.add(ProfileNavRoute.Profile(user))
        }

        override fun navigateToCompare(targetUser: User) {
            backstack.add(ProfileNavRoute.MediaComparison(targetUser))
        }

        override fun navigateToSettings() {
            backstack.add(ProfileNavRoute.Settings)
        }

        override fun navigateToAbout() {
            backstack.add(ProfileNavRoute.AboutApp)
        }

        override fun navigateBack() {
            if(backstack.size > 1) backstack.removeLastOrNull()
        }

        override fun navigateToDetails(detailsNavRoute: DetailsNavRoute) {
            mainNavOptions?.let {
                mainNavOptions.navigateToDetails(detailsNavRoute)
            } ?: backstack.add(ProfileNavRoute.Details(detailsNavRoute))
        }
    }

    NavDisplay(
        backStack = backstack,
        onBack = { backstack.removeLastOrNull() },
        entryProvider = entryProvider {
            entry<ProfileNavRoute.Profile> { route ->
                ProfileScreen(
                    userData = route.user,
                    navOptions = profileNavOptions
                )
            }
            entry<ProfileNavRoute.Details> { route ->
                DetailsNavigator(
                    detailsNavRoute = route.detailsNavRoute,
                    mainNavOptions = profileNavOptions
                )
            }
            entry<ProfileNavRoute.Settings> {
                SettingsScreen()
            }
            entry<ProfileNavRoute.AboutApp> {
                AboutAppScreen()
            }
            entry<ProfileNavRoute.MediaComparison> { route ->
                CompareScreen(
                    targetUser = route.targetUser,
                    navOptions = profileNavOptions
                )
            }
        },
        transitionSpec = {
            slideInHorizontally(
                initialOffsetX = { it },
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioNoBouncy,
                    stiffness = Spring.StiffnessMediumLow
                )
            ) togetherWith ExitTransition.KeepUntilTransitionsFinished
        },
        popTransitionSpec = {
            EnterTransition.None togetherWith slideOutHorizontally(
                targetOffsetX = { it },
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
}