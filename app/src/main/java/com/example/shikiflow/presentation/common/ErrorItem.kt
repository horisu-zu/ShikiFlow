package com.example.shikiflow.presentation.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.shikiflow.domain.model.common.ErrorFaces
import com.example.shikiflow.utils.IconResource
import com.example.shikiflow.utils.toIcon

@Composable
fun ErrorItem(
    message: String,
    modifier: Modifier = Modifier,
    showFace: Boolean = true,
    icon: IconResource = IconResource.Vector(Icons.Default.Refresh),
    buttonLabel: String? = null,
    onButtonClick: () -> Unit = { /**/ }
) {
    val errorFace = ErrorFaces.facesList.random()

    Column(
        modifier = modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp, Alignment.Top)
    ) {
        if(showFace) {
            Text(
                text = errorFace,
                style = MaterialTheme.typography.displayMedium,
                textAlign = TextAlign.Center
            )
        }
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center
        )
        buttonLabel?.let { label ->
            Button(
                onClick = onButtonClick,
                modifier = Modifier.wrapContentWidth().clip(CircleShape)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterHorizontally)
                ) {
                    icon.toIcon(
                        modifier = Modifier.size(24.dp),
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                    Text(
                        text = label,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Medium
                        )
                    )
                }
            }
        }
    }
}