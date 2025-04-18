package com.example.shikiflow.presentation.screen.more

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.graphql.CurrentUserQuery
import com.example.shikiflow.presentation.screen.more.history.HistoryScreen
import com.example.shikiflow.presentation.screen.more.profile.ProfileScreen
import com.example.shikiflow.presentation.screen.more.settings.SettingsScreen
import com.example.shikiflow.utils.Animations.slideInFromBottom
import com.example.shikiflow.utils.Animations.slideInFromLeft
import com.example.shikiflow.utils.Animations.slideInFromRight
import com.example.shikiflow.utils.Animations.slideInFromTop
import com.example.shikiflow.utils.Animations.slideOutToBottom
import com.example.shikiflow.utils.Animations.slideOutToLeft
import com.example.shikiflow.utils.Animations.slideOutToRight
import com.example.shikiflow.utils.Animations.slideOutToTop

@Composable
fun MoreScreenNavigator(
    currentUser: CurrentUserQuery.Data?
) {
    val moreNavController = rememberNavController()

    NavHost(
        navController = moreNavController,
        startDestination = "moreMainScreen"
    ) {
        composable(
            route = "moreMainScreen",
            enterTransition = { slideInFromRight() },
            exitTransition = { slideOutToLeft() },
            popEnterTransition = { slideInFromLeft() },
            popExitTransition = { slideOutToRight() }
        ) {
            MoreScreen(
                navController = moreNavController,
                currentUser = currentUser
            )
        }
        composable(
            route = "profileScreen",
            enterTransition = { slideInFromRight() },
            exitTransition = { slideOutToLeft() },
            popEnterTransition = { slideInFromLeft() },
            popExitTransition = { slideOutToRight() }
        ) {
            ProfileScreen(
                navController = moreNavController,
                currentUser = currentUser
            )
        }
        composable(
            route = "historyScreen",
            enterTransition = { slideInFromRight() },
            exitTransition = { slideOutToLeft() },
            popEnterTransition = { slideInFromLeft() },
            popExitTransition = { slideOutToRight() }
        ) {
            HistoryScreen(
                userData = currentUser,
                navController = moreNavController
            )
        }
        composable(
            route = "settingsScreen",
            enterTransition = { slideInFromBottom() },
            exitTransition = { slideOutToTop() },
            popEnterTransition = { slideInFromTop() },
            popExitTransition = { slideOutToBottom() }
        ) {
            SettingsScreen(
                navController = moreNavController
            )
        }
    }
}