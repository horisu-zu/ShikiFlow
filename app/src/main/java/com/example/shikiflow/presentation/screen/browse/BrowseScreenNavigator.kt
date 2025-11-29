package com.example.shikiflow.presentation.screen.browse

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.example.shikiflow.domain.model.anime.BrowseType
import com.example.shikiflow.domain.model.tracks.MediaType
import com.example.shikiflow.domain.model.user.User
import com.example.shikiflow.presentation.screen.main.details.DetailsNavigator

@Composable
fun BrowseScreenNavigator(
    browseScreenBackStack: NavBackStack<NavKey>,
    currentUserData: User?
) {
    //val browseBackstack = rememberNavBackStack(BrowseNavRoute.BrowseScreen)
    val browseNavOptions = object: BrowseNavOptions {
        override fun navigateToSideScreen(browseType: BrowseType) {
            browseScreenBackStack.add(BrowseNavRoute.SideScreen(browseType))
        }

        override fun navigateToDetails(mediaId: String, mediaType: MediaType) {
            browseScreenBackStack.add(BrowseNavRoute.Details(mediaId, mediaType))
        }

        override fun navigateBack() { browseScreenBackStack.removeLastOrNull() }
    }

    NavDisplay(
        backStack = browseScreenBackStack,
        onBack = { browseScreenBackStack.removeLastOrNull() },
        entryProvider = entryProvider {
            entry<BrowseNavRoute.BrowseScreen> {
                BrowseScreen(
                    browseNavOptions = browseNavOptions
                )
            }
            entry<BrowseNavRoute.SideScreen> { route ->
                BrowseSideScreen(
                    browseType = route.browseType,
                    navOptions = browseNavOptions,
                    onBackNavigate = { browseScreenBackStack.removeLastOrNull() }
                )
            }
            entry<BrowseNavRoute.Details> { route ->
                DetailsNavigator(
                    currentUserData = currentUserData,
                    mediaId = route.mediaId,
                    mediaType = route.mediaType,
                    source = "browse"
                )
            }
        }, entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator()
        )
    )
}