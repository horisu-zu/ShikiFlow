package com.example.shikiflow.presentation.common

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.example.shikiflow.utils.IconResource
import com.example.shikiflow.utils.toIcon

@Composable
fun CardItem(
    item: String,
    modifier: Modifier = Modifier,
    style: TextStyle = MaterialTheme.typography.labelMedium,
) {
    Box(
        modifier = modifier.clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.primaryContainer),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = item,
            style = style.copy(
                color = MaterialTheme.colorScheme.onSurface
            ),
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
        )
    }
    /*Card(
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        onClick = { *//*TODO*//* },
        modifier = modifier
    ) {
        Text(
            text = item,
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
        )
    }*/
}

@Composable
fun NavigationCard(
    icon: IconResource,
    title: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.25f))
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 18.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        icon.toIcon(
            modifier = Modifier.size(24.dp),
            tint = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = title,
            style = MaterialTheme.typography.labelLarge
        )
    }
}