package com.example.shikiflow.presentation.common

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun TriFilterChip(
    text: String,
    value: Boolean?,
    onValueChanged: (Boolean?) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    FilterChip(
        selected = value != null,
        onClick = {
            onValueChanged(
                when (value) {
                    null -> true
                    true -> false
                    false -> null
                }
            )
        },
        label = { Text(text = text) },
        modifier = modifier,
        enabled = enabled,
        leadingIcon = {
            when(value) {
                true -> {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Check"
                    )
                }
                false -> {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = "Clear"
                    )
                }
                else -> null
            }
        },
        colors = if (value == false) {
            FilterChipDefaults.filterChipColors(
                containerColor = MaterialTheme.colorScheme.errorContainer,
                labelColor = MaterialTheme.colorScheme.onErrorContainer,
                iconColor = MaterialTheme.colorScheme.onErrorContainer,
                selectedContainerColor = MaterialTheme.colorScheme.errorContainer,
                selectedLabelColor = MaterialTheme.colorScheme.onErrorContainer,
                selectedLeadingIconColor = MaterialTheme.colorScheme.onErrorContainer,
            )
        } else {
            FilterChipDefaults.filterChipColors()
        }
    )
}
