package com.example.shikiflow.presentation.screen.main

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
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.example.shikiflow.domain.model.auth.AuthType
import com.example.shikiflow.domain.model.tracks.MediaType
import com.example.shikiflow.domain.model.user.User
import com.example.shikiflow.presentation.screen.MainScreenNavOptions
import com.example.shikiflow.presentation.screen.MainScreenNavRoute
import com.example.shikiflow.presentation.screen.main.details.DetailsNavigator

@Composable
fun MainScreenNavigator(
    mainScreenBackStack: NavBackStack<NavKey>,
    currentUserData: User?,
    authType: AuthType
) {
    val options = object : MainScreenNavOptions {
        override fun navigateToDetails(mediaId: Int, mediaType: MediaType) {
            mainScreenBackStack.add(MainScreenNavRoute.Details(mediaId, mediaType))
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
                    mediaId = route.mediaId,
                    mediaType = route.mediaType,
                    source = "main"
                )
            }
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
