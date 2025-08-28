package com.example.shikiflow.presentation.screen.more.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp

data class BottomSheetConfig(
    val title: String,
    val options: List<String>,
    val currentValue: String,
    val onOptionClick: (String) -> Unit
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsBottomSheet(
    title: String,
    options: List<String>,
    currentValue: String,
    onOptionClick: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState()

    ModalBottomSheet(
        sheetState = sheetState,
        onDismissRequest = onDismiss
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge
            )
            options.forEach { value ->
                Row(
                    modifier = Modifier.fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .clickable {
                            if(value != currentValue) onOptionClick(value)
                        },
                    horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.Start),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = (value == currentValue),
                        onClick = { /**/ }
                    )
                    Text(
                        text = value,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}