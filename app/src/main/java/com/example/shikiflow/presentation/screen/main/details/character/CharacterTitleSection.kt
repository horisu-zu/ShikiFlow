package com.example.shikiflow.presentation.screen.main.details.character

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.shikiflow.presentation.common.image.RoundedImage

@Composable
fun CharacterTitleSection(
    avatarUrl: String,
    name: String?,
    japaneseName: String?
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.Start)
    ) {
        RoundedImage(
            model = avatarUrl,
            modifier = Modifier.size(120.dp)
        )
        Column {
            name?.let { englishName ->
                Text(
                    text = englishName,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
            japaneseName?.let { japaneseName ->
                Text(
                    text = japaneseName,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.75f)
                    )
                )
            }
        }
    }
}