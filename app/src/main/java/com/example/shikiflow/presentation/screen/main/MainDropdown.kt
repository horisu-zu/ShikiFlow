package com.example.shikiflow.presentation.screen.main

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp

@Composable
fun MainDropdown(
    expanded: Boolean,
    currentTrackMode: MainTrackMode,
    onModeChange: (MainTrackMode) -> Unit,
    onDismiss: () -> Unit
) {
    val modeOptions = MainTrackMode.entries.toList()

    DropdownMenu(
        expanded = expanded,
        onDismissRequest = { onDismiss() },
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.selectableGroup(),
            verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.Top)
        ) {
            modeOptions.forEach { mode ->
                Row(
                    modifier = Modifier.fillMaxWidth()
                        .selectable(
                            selected = mode == currentTrackMode,
                            onClick = {
                                onModeChange(mode)
                                onDismiss()
                            },
                            role = Role.RadioButton
                        ).padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.Start),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = (mode == currentTrackMode),
                        onClick = null
                    )
                    Text(
                        text = mode.displayValue,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}

enum class MainTrackMode(val displayValue: String) {
    ANIME("Anime"),
    MANGA("Manga & Ranobe")
}