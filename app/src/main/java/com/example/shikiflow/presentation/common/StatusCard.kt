package com.example.shikiflow.presentation.common

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.shikiflow.ui.theme.AldrichFont

@Composable
fun StatusCard(
    text: String,
    modifier: Modifier = Modifier,
    fontSize: TextUnit = 12.sp,
    color: Color = MaterialTheme.colorScheme.surface
) {
    Card(
        modifier = modifier.clip(RoundedCornerShape(8.dp)),
        colors = CardDefaults.cardColors(
            containerColor = color
        )
    ) {
        Text(
            text = text,
            fontSize = fontSize,
            fontFamily = AldrichFont,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
        )
    }
}