package com.example.shikiflow.presentation.common

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp

@Composable
fun CardItem(
    item: String,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    style: TextStyle = MaterialTheme.typography.labelMedium,
) {
    Box(
        modifier = modifier.clip(RoundedCornerShape(8.dp))
            .then(
                other = if(onClick != null) {
                    Modifier.clickable(onClick = onClick)
                } else Modifier
            ).background(MaterialTheme.colorScheme.primaryContainer),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = item,
            style = style.copy(
                color = MaterialTheme.colorScheme.onPrimaryContainer
            ),
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
        )
    }
}