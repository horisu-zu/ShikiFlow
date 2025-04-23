package com.example.shikiflow.presentation.screen.browse

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.shikiflow.data.anime.BrowseType
import com.example.shikiflow.utils.Animations.slideInFromLeft
import com.example.shikiflow.utils.Animations.slideInFromRight
import com.example.shikiflow.utils.Animations.slideOutToLeft
import com.example.shikiflow.utils.Animations.slideOutToRight
import com.example.shikiflow.utils.CustomNavType
import kotlinx.serialization.json.Json
import kotlin.reflect.typeOf

@Composable
fun BrowseScreenNavigator(
    rootNavController: NavController
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
            BrowseScreen(
                browseNavController = browseNavController,
                rootNavController = rootNavController
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
                rootNavController = rootNavController,
                browseNavController = browseNavController
            )
        }
    }
}