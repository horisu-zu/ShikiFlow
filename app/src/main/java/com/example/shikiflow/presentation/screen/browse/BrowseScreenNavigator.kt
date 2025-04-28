package com.example.shikiflow.presentation.screen.browse

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.shikiflow.data.anime.BrowseType
import com.example.shikiflow.presentation.screen.MediaNavOptions
import com.example.shikiflow.utils.Animations.slideInFromLeft
import com.example.shikiflow.utils.Animations.slideInFromRight
import com.example.shikiflow.utils.Animations.slideOutToLeft
import com.example.shikiflow.utils.Animations.slideOutToRight
import com.example.shikiflow.utils.CustomNavType
import kotlinx.serialization.json.Json
import kotlin.reflect.typeOf

@Composable
fun BrowseScreenNavigator(
    navOptions: MediaNavOptions
) {
    val browseNavController = rememberNavController()
    val json = Json {
        useArrayPolymorphism = true
    }

    NavHost(
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
    }
}