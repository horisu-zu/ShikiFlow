package com.example.shikiflow.presentation.common.scene

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.scene.Scene

class TwoPaneScene<T: Any>(
    val listEntry: NavEntry<T>,
    val detailEntry: NavEntry<T>,
    override val key: Any,
    override val previousEntries: List<NavEntry<T>>
): Scene<T> {

    override val entries: List<NavEntry<T>> = listOf(listEntry, detailEntry)

    override val content: @Composable (() -> Unit) = {
        Row(modifier = Modifier.fillMaxSize()) {
            Column(modifier = Modifier.weight(0.6f)) {
                listEntry.Content()
            }
            Column(modifier = Modifier.weight(0.4f)) {
                detailEntry.Content()
            }
        }
    }

    companion object {
        internal const val LIST_KEY = "TwoPaneScene-List"
        internal const val DETAIL_KEY = "TwoPaneScene-Detail"

        fun listPane() = mapOf(LIST_KEY to true)
        fun detailPane() = mapOf(DETAIL_KEY to true)
    }
}