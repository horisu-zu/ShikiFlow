package com.example.shikiflow.presentation.screen.main

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
import com.example.shikiflow.presentation.screen.MainScreenNavOptions
import com.example.shikiflow.presentation.screen.MainScreenNavRoute
import com.example.shikiflow.presentation.screen.main.details.DetailsNavRoute
import com.example.shikiflow.presentation.screen.main.details.DetailsNavigator
import com.example.shikiflow.presentation.screen.more.profile.ProfileNavigator

@Composable
fun MainScreenNavigator(
    mainScreenBackStack: NavBackStack<NavKey>,
    currentUserData: User?,
    authType: AuthType
) {
    val options = object : MainScreenNavOptions {
        override fun navigateToDetails(detailsNavRoute: DetailsNavRoute) {
            mainScreenBackStack.add(MainScreenNavRoute.Details(detailsNavRoute))
        }

        override fun navigateToProfile(user: User?) {
            mainScreenBackStack.add(MainScreenNavRoute.Profile(user))
        }

        override fun navigateBack() {
            mainScreenBackStack.removeLastOrNull()
        }
    }

    NavDisplay(
        backStack = mainScreenBackStack,
        onBack = { mainScreenBackStack.removeLastOrNull() },
        entryProvider = entryProvider {
            entry<MainScreenNavRoute.MainTracks> {
                MainScreen(
                    currentUser = currentUserData,
                    navOptions = options
                )
            }
            entry<MainScreenNavRoute.Details> { route ->
                DetailsNavigator(
                    currentUserData = currentUserData,
                    authType = authType,
                    detailsNavRoute = route.detailsNavRoute,
                    navOptions = options
                )
            }
            entry<MainScreenNavRoute.Profile> { route ->
                ProfileNavigator(
                    user = route.user,
                    mainNavOptions = options
                )
            }
        },
        transitionSpec = {
            fadeIn(
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioNoBouncy,
                    stiffness = Spring.StiffnessLow
                )
            ) togetherWith fadeOut(
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioNoBouncy,
                    stiffness = Spring.StiffnessLow
                )
            )
        },
        popTransitionSpec = {
            fadeIn(
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioNoBouncy,
                    stiffness = Spring.StiffnessLow
                )
            ) togetherWith fadeOut(
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioNoBouncy,
                    stiffness = Spring.StiffnessLow
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
