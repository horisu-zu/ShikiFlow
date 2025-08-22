package com.example.shikiflow.presentation.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.shikiflow.data.common.ErrorFaces

@Composable
fun ErrorItem(
    message: String,
    modifier: Modifier = Modifier,
    buttonLabel: String? = null,
    onButtonClick: () -> Unit = { /**/ }
) {
    val errorFace = ErrorFaces.facesList.random()

    Column(
        modifier = modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp, Alignment.Top)
    ) {
        Text(
            text = errorFace,
            style = MaterialTheme.typography.displayMedium,
            textAlign = TextAlign.Center
        )
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center
        )
        buttonLabel?.let {
            Button(
                label = buttonLabel,
                onClick = onButtonClick
            )
        }
    }
}