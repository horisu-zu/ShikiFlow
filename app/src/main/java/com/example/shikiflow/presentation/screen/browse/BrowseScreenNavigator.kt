package com.example.shikiflow.presentation.screen.browse

import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.entry
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.example.shikiflow.data.anime.BrowseType
import com.example.shikiflow.presentation.screen.MediaNavOptions
import kotlinx.serialization.json.Json

@Composable
fun BrowseScreenNavigator(
    navOptions: MediaNavOptions
) {
    val browseBackstack = rememberNavBackStack(BrowseNavRoute.BrowseScreen)
    //val browseNavController = rememberNavController()
    val json = Json {
        useArrayPolymorphism = true
    }
    val browseNavOptions = object: BrowseNavOptions {
        override fun navigateToSideScreen(browseType: BrowseType) {
            browseBackstack.add(BrowseNavRoute.SideScreen(browseType))
        }
        override fun navigateBack() { browseBackstack.removeLastOrNull() }
    }

    NavDisplay(
        backStack = browseBackstack,
        onBack = { browseBackstack.removeLastOrNull() },
        entryProvider = entryProvider {
            entry<BrowseNavRoute.BrowseScreen> {
                BrowseScreen(
                    browseNavOptions = browseNavOptions,
                    navOptions = navOptions
                )
            }
            entry<BrowseNavRoute.SideScreen> { route ->
                BrowseSideScreen(
                    browseType = route.browseType,
                    navOptions = navOptions,
                    onBackNavigate = { browseBackstack.removeLastOrNull() }
                )
            }
        }
    )

    /*NavHost(
        startDestination = BrowseNavRoute.BrowseScreen,
        navController = browseNavController
    ) {
        composable<BrowseNavRoute.BrowseScreen>(
            enterTransition = { slideInFromRight() },
            exitTransition = { slideOutToLeft() },
            popEnterTransition = { slideInFromLeft() },
            popExitTransition = { slideOutToRight() }
        ) {
            val browseNavOptions = object: BrowseNavOptions {
                override fun navigateToSideScreen(browseType: BrowseType) {
                    browseNavController.navigate(BrowseNavRoute.SideScreen(browseType))
                }
                override fun navigateBack() { browseNavController.popBackStack() }
            }

            BrowseScreen(
                browseNavOptions = browseNavOptions,
                navOptions = navOptions
            )
        }
        composable<BrowseNavRoute.SideScreen>(
            typeMap = mapOf(
                typeOf<BrowseType>() to CustomNavType.navTypeOf<BrowseType>(json = json)
            ),
            enterTransition = { slideInFromRight() },
            exitTransition = { slideOutToLeft() },
            popEnterTransition = { slideInFromLeft() },
            popExitTransition = { slideOutToRight() }
        ) { backStackEntry ->
            val args = backStackEntry.toRoute<BrowseNavRoute.SideScreen>()
            BrowseSideScreen(
                browseType = args.browseType,
                navOptions = navOptions,
                onBackNavigate = { browseNavController.popBackStack() }
            )
        }
    }*/
}