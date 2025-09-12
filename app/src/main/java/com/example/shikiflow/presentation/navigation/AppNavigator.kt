package com.example.shikiflow.presentation.navigation

import android.util.Log
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation3.runtime.entry
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.example.shikiflow.presentation.auth.AuthScreen
import com.example.shikiflow.presentation.screen.MainNavigator
import com.example.shikiflow.presentation.screen.main.details.anime.watch.player.PlayerScreen
import com.example.shikiflow.presentation.viewmodel.AuthState
import com.example.shikiflow.presentation.viewmodel.AuthViewModel

@Composable
fun AppNavigator(
    onFinishActivity: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val appBackstack = rememberNavBackStack(AppNavRoute.Splash)
    val authState by viewModel.authState.collectAsState()

    val options = object : AppNavOptions {
        override fun navigateToAuth() {
            appBackstack.replaceAll { AppNavRoute.Auth }
        }

        override fun navigateToMain() {
            appBackstack.replaceAll { AppNavRoute.Main }
        }

        override fun navigateToPlayer(
            title: String,
            link: String,
            translationGroup: String,
            serialNum: Int,
            offset: Int,
            episodesCount: Int
        ) {
            if(appBackstack.lastOrNull() is AppNavRoute.Player) {
                appBackstack.removeLastOrNull()
            }
            appBackstack.add(AppNavRoute.Player(title, link, translationGroup, serialNum + offset, episodesCount))
        }

        override fun navigateBack() {
            if(appBackstack.size > 1) appBackstack.removeLastOrNull()
        }
    }

    LaunchedEffect(authState) {
        when(authState) {
            AuthState.Success -> {
                Log.d("AppNavigator", "Navigating to main")
                options.navigateToMain()
            }
            AuthState.Initial -> {
                Log.d("AppNavigator", "Navigating to auth")
                options.navigateToAuth()
            }
            else -> {}
        }
    }

    NavDisplay(
        backStack = appBackstack,
        onBack = { if(appBackstack.size > 1) appBackstack.removeLastOrNull() },
        entryProvider = entryProvider {
            entry<AppNavRoute.Splash> {
                /**/
            }
            entry<AppNavRoute.Auth> {
                AuthScreen()
            }
            entry<AppNavRoute.Main> {
                MainNavigator(
                    appNavOptions = options,
                    onFinishActivity = onFinishActivity
                )
            }
            entry<AppNavRoute.Player> { route ->
                PlayerScreen(
                    title = route.title,
                    link = route.link,
                    translationGroup = route.translationGroup,
                    serialNum = route.serialNum,
                    episodesCount = route.episodesCount,
                    navOptions = options
                )
            }
        },
        transitionSpec = {
            fadeIn(initialAlpha = 0.1f, animationSpec = tween(500)) togetherWith
                    ExitTransition.KeepUntilTransitionsFinished
        },
        popTransitionSpec = {
            EnterTransition.None togetherWith
                    fadeOut(targetAlpha = 0.1f, animationSpec = tween(500))
        },
        predictivePopTransitionSpec = {
            EnterTransition.None togetherWith
                    fadeOut(targetAlpha = 0.1f, animationSpec = tween(500))
        }
    )
}