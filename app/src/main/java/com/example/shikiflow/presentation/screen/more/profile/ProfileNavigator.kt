package com.example.shikiflow.presentation.screen.more.profile

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.example.shikiflow.domain.model.user.User
import com.example.shikiflow.presentation.screen.MainScreenNavOptions
import com.example.shikiflow.presentation.screen.main.details.DetailsNavRoute
import com.example.shikiflow.presentation.screen.more.compare.CompareScreen

@Composable
fun ProfileNavigator(
    user: User?,
    mainNavOptions: MainScreenNavOptions
) {
    val profileBackstack = rememberNavBackStack(ProfileNavRoute.Profile(user))

    val profileNavOptions = object : ProfileNavOptions {
        override fun navigateToProfile(user: User?) {
            profileBackstack.add(ProfileNavRoute.Profile(user))
        }

        override fun navigateToCompare(targetUser: User) {
            profileBackstack.add(ProfileNavRoute.MediaComparison(targetUser))
        }

        override fun navigateBack() {
            if(profileBackstack.size > 1) profileBackstack.removeLastOrNull()
        }

        override fun navigateToDetails(detailsNavRoute: DetailsNavRoute) {
            mainNavOptions.navigateToDetails(detailsNavRoute)
        }
    }

    NavDisplay(
        backStack = profileBackstack,
        onBack = { profileBackstack.removeLastOrNull() },
        entryProvider = entryProvider {
            entry<ProfileNavRoute.Profile> { route ->
                ProfileScreen(
                    userData = route.user,
                    navOptions = profileNavOptions
                )
            }
            entry<ProfileNavRoute.MediaComparison> (
                metadata = NavDisplay.transitionSpec {
                    slideInHorizontally(
                        initialOffsetX = { it },
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioNoBouncy,
                            stiffness = Spring.StiffnessMediumLow
                        )
                    ) togetherWith ExitTransition.KeepUntilTransitionsFinished
                } + NavDisplay.popTransitionSpec {
                    EnterTransition.None togetherWith slideOutHorizontally(
                        targetOffsetX = { it },
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioNoBouncy,
                            stiffness = Spring.StiffnessMediumLow
                        )
                    )
                } + NavDisplay.predictivePopTransitionSpec {
                    EnterTransition.None togetherWith slideOutHorizontally(
                        targetOffsetX = { it },
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioNoBouncy,
                            stiffness = Spring.StiffnessMediumLow
                        )
                    )
                }
            ) { route ->
                CompareScreen(
                    currentUser = user,
                    targetUser = route.targetUser,
                    navOptions = profileNavOptions
                )
            }
        }
    )
}