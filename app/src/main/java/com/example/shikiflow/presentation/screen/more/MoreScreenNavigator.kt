package com.example.shikiflow.presentation.screen.more

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.shikiflow.presentation.screen.more.profile.ProfileScreen
import com.example.shikiflow.presentation.viewmodel.user.UserViewModel
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
    userViewModel: UserViewModel = hiltViewModel(),
    mainNavController: NavController
) {
    val moreNavController = rememberNavController()
    val currentUser by userViewModel.currentUserData.collectAsState()

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
                mainNavController = mainNavController,
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