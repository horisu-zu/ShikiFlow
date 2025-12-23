package com.example.shikiflow.presentation.common.scene

import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.scene.Scene
import androidx.navigation3.scene.SceneStrategy
import androidx.navigation3.scene.SceneStrategyScope
import androidx.window.core.layout.WindowSizeClass
import androidx.window.core.layout.WindowSizeClass.Companion.WIDTH_DP_MEDIUM_LOWER_BOUND

class TwoPaneSceneStrategy<T: Any>(val windowSizeClass: WindowSizeClass): SceneStrategy<T> {
    override fun SceneStrategyScope<T>.calculateScene(entries: List<NavEntry<T>>): Scene<T>? {

        if(!windowSizeClass.isWidthAtLeastBreakpoint(WIDTH_DP_MEDIUM_LOWER_BOUND)) {
            return null
        }

        val detailEntry = entries.lastOrNull()
            ?.takeIf { entry ->
                entry.metadata.containsKey(TwoPaneScene.DETAIL_KEY)
            } ?: return null
        val listEntry = entries.findLast { entry ->
            entry.metadata.containsKey(TwoPaneScene.LIST_KEY)
        } ?: return null

        val sceneKey = listEntry.contentKey

        return TwoPaneScene(
            key = sceneKey,
            previousEntries = entries.dropLast(1),
            listEntry = listEntry,
            detailEntry = detailEntry
        )
    }
}

@Composable
fun <T: Any> rememberTwoPaneSceneStrategy(): TwoPaneSceneStrategy<T> {
    val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass

    return remember(windowSizeClass) {
        TwoPaneSceneStrategy(windowSizeClass)
    }
}