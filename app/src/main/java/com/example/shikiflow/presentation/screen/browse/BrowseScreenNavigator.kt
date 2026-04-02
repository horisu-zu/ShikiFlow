package com.example.shikiflow.presentation.screen.browse

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
import com.example.shikiflow.domain.model.browse.BrowseType
import com.example.shikiflow.domain.model.user.User
import com.example.shikiflow.presentation.screen.browse.main.BrowseScreen
import com.example.shikiflow.presentation.screen.browse.side.BrowseSideScreen
import com.example.shikiflow.presentation.screen.main.details.DetailsNavRoute
import com.example.shikiflow.presentation.screen.main.details.DetailsNavigator
import com.example.shikiflow.presentation.screen.more.profile.ProfileNavigator

@Composable
fun BrowseScreenNavigator(
    browseBackStack: NavBackStack<NavKey>
) {
    val browseNavOptions = object: BrowseNavOptions {
        override fun navigateToSideScreen(browseType: BrowseType) {
            browseBackStack.add(BrowseNavRoute.SideScreen(browseType))
        }

        override fun navigateToDetails(detailsNavRoute: DetailsNavRoute) {
            browseBackStack.add(BrowseNavRoute.Details(detailsNavRoute))
        }

        override fun navigateToProfile(user: User?) {
            browseBackStack.add(BrowseNavRoute.Profile(user))
        }

        override fun navigateBack() { browseBackStack.removeLastOrNull() }
    }

    NavDisplay(
        backStack = browseBackStack,
        onBack = { browseBackStack.removeLastOrNull() },
        entryProvider = entryProvider {
            entry<BrowseNavRoute.BrowseScreen> {
                BrowseScreen(
                    browseNavOptions = browseNavOptions
                )
                /*BrowseScreen(
                    browseNavOptions = browseNavOptions
                )*/
            }
            entry<BrowseNavRoute.SideScreen> { route ->
                BrowseSideScreen(
                    browseType = route.browseType,
                    navOptions = browseNavOptions
                )
            }
            entry<BrowseNavRoute.Details> { route ->
                DetailsNavigator(
                    detailsNavRoute = route.detailsNavRoute,
                    mainNavOptions = browseNavOptions
                )
            }
            entry<BrowseNavRoute.Profile> { route ->
                ProfileNavigator(
                    user = route.user,
                    mainNavOptions = browseNavOptions
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