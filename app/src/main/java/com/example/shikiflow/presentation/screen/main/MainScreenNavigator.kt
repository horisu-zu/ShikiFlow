package com.example.shikiflow.presentation.screen.main

import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.entry
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.example.graphql.CurrentUserQuery
import com.example.shikiflow.domain.model.tracks.MediaType
import com.example.shikiflow.presentation.screen.MainScreenNavOptions
import com.example.shikiflow.presentation.screen.MainScreenNavRoute
import com.example.shikiflow.presentation.screen.main.details.DetailsNavigator
import com.example.shikiflow.utils.AppSettingsManager

@Composable
fun MainScreenNavigator(
    appSettingsManager: AppSettingsManager,
    currentUserData: CurrentUserQuery.Data?
) {
    val mainScreenBackstack = rememberNavBackStack(MainScreenNavRoute.MainTracks)
    val options = object : MainScreenNavOptions {
        override fun navigateToDetails(mediaId: String, mediaType: MediaType) {
            mainScreenBackstack.add(MainScreenNavRoute.Details(mediaId, mediaType))
        }

        override fun navigateBack() {
            mainScreenBackstack.removeLastOrNull()
        }
    }

    NavDisplay(
        backStack = mainScreenBackstack,
        onBack = { mainScreenBackstack.removeLastOrNull() },
        entryProvider = entryProvider {
            entry<MainScreenNavRoute.MainTracks> {
                MainScreen(
                    appSettingsManager = appSettingsManager,
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
        }
    )
}
