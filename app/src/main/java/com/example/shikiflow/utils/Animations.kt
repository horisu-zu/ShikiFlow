package com.example.shikiflow.utils

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically

object Animations {
    fun slideInFromRight(): EnterTransition {
        return slideInHorizontally(
            initialOffsetX = { fullWidth -> fullWidth },
            animationSpec = tween(300)
        )
    }

    fun slideOutToLeft(): ExitTransition {
        return slideOutHorizontally(
            targetOffsetX = { fullWidth -> -fullWidth },
            animationSpec = tween(300)
        )
    }

    fun slideInFromLeft(): EnterTransition {
        return slideInHorizontally(
            initialOffsetX = { fullWidth -> -fullWidth },
            animationSpec = tween(300)
        )
    }

    fun slideOutToRight(): ExitTransition {
        return slideOutHorizontally(
            targetOffsetX = { fullWidth -> fullWidth },
            animationSpec = tween(300)
        )
    }

    fun slideInFromBottom(): EnterTransition {
        return slideInVertically(
            initialOffsetY = { fullHeight -> fullHeight },
            animationSpec = tween(300)
        )
    }

    fun slideOutToTop(): ExitTransition {
        return slideOutVertically(
            targetOffsetY = { fullHeight -> -fullHeight },
            animationSpec = tween(300)
        )
    }

    fun slideInFromTop(): EnterTransition {
        return slideInVertically(
            initialOffsetY = { fullHeight -> -fullHeight },
            animationSpec = tween(300)
        )
    }

    fun slideOutToBottom(): ExitTransition {
        return slideOutVertically(
            targetOffsetY = { fullHeight -> fullHeight },
            animationSpec = tween(300)
        )
    }
}