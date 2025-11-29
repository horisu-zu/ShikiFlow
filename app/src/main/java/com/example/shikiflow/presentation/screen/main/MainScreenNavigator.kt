package com.example.shikiflow.presentation.screen.main

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.example.shikiflow.domain.model.tracks.MediaType
import com.example.shikiflow.domain.model.user.User
import com.example.shikiflow.presentation.screen.MainScreenNavOptions
import com.example.shikiflow.presentation.screen.MainScreenNavRoute
import com.example.shikiflow.presentation.screen.main.details.DetailsNavigator

@Composable
fun MainScreenNavigator(
    mainScreenBackStack: NavBackStack<NavKey>,
    currentUserData: User?
) {
    val options = object : MainScreenNavOptions {
        override fun navigateToDetails(mediaId: String, mediaType: MediaType) {
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
                    mediaId = route.mediaId,
                    mediaType = route.mediaType,
                    source = "main"
                )
            }
        }, entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator()
        )
    )
}
