package com.example.shikiflow.presentation.common

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun CardItem(
    item: String
) {
    Card(
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        onClick = { /*TODO*/ }
    ) {
        Text(
            text = item,
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(horizontal = 4.dp, vertical = 8.dp)
        )
    }
}