package com.example.shikiflow.presentation.navigation

import android.util.Log
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.example.shikiflow.domain.model.auth.AuthType
import com.example.shikiflow.presentation.auth.AuthScreen
import com.example.shikiflow.presentation.screen.MainNavigator
import com.example.shikiflow.presentation.viewmodel.AuthState

@Composable
fun AppNavigator(
    authState: AuthState,
    onAuthorize: (AuthType) -> String,
    onFinishActivity: () -> Unit
) {
    val startKey = remember {
        when(authState) {
            AuthState.Success -> AppNavRoute.Main
            else -> AppNavRoute.Auth
        }
    }
    val appBackstack = rememberNavBackStack(startKey)

    val options = object : AppNavOptions {
        override fun navigateToAuth() {
            appBackstack.replaceAll { AppNavRoute.Auth }
        }

        override fun navigateToMain() {
            appBackstack.replaceAll { AppNavRoute.Main }
        }
    }

    LaunchedEffect(authState) {
        when(authState) {
            AuthState.Success -> {
                Log.d("AppNavigator", "Navigating to main")
                options.navigateToMain()
            }
            else -> {
                Log.d("AppNavigator", "Navigating to auth")
                options.navigateToAuth()
            }
        }
    }

    NavDisplay(
        backStack = appBackstack,
        onBack = { if(appBackstack.size > 1) appBackstack.removeLastOrNull() },
        entryProvider = entryProvider {
            entry<AppNavRoute.Auth> {
                AuthScreen(
                    onAuth = { authType ->
                        onAuthorize(authType)
                    }
                )
            }
            entry<AppNavRoute.Main> {
                MainNavigator(
                    onMoveToBack = onFinishActivity
                )
            }
        },
        transitionSpec = {
            fadeIn(
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioNoBouncy,
                    stiffness = Spring.StiffnessMediumLow
                )
            ) togetherWith ExitTransition.None
        },
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator()
        )
    )
}