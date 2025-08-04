package com.example.shikiflow.presentation.screen.main.details.manga.read

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

sealed interface MangaReadNavRoute : NavKey {

    @Serializable
    data class ChaptersScreen(
        val mangaDexId: String,
        val title: String,
        val completedChapters: Int
    ) : MangaReadNavRoute

    // Chapter Translation Choice Screen
    // Chapter Reader Screen
}