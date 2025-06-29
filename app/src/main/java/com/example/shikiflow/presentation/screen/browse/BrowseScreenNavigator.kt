package com.example.shikiflow.presentation.screen.browse

import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.entry
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.example.graphql.CurrentUserQuery
import com.example.shikiflow.data.anime.BrowseType
import com.example.shikiflow.data.tracks.MediaType
import com.example.shikiflow.presentation.screen.main.details.DetailsNavigator

@Composable
fun BrowseScreenNavigator(
    currentUserData: CurrentUserQuery.Data?
) {
    val browseBackstack = rememberNavBackStack(BrowseNavRoute.BrowseScreen)
    val browseNavOptions = object: BrowseNavOptions {
        override fun navigateToSideScreen(browseType: BrowseType) {
            browseBackstack.add(BrowseNavRoute.SideScreen(browseType))
        }

        override fun navigateToDetails(mediaId: String, mediaType: MediaType) {
            browseBackstack.add(BrowseNavRoute.Details(mediaId, mediaType))
        }

        override fun navigateBack() { browseBackstack.removeLastOrNull() }
    }

    NavDisplay(
        backStack = browseBackstack,
        onBack = { browseBackstack.removeLastOrNull() },
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
                    onBackNavigate = { browseBackstack.removeLastOrNull() }
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
        }
    )
}