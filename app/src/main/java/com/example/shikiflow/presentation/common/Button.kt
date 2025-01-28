package com.example.shikiflow.presentation.common

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.shikiflow.utils.IconResource

@Composable
fun Button(
    modifier: Modifier = Modifier,
    label: String = "Confirm operation",
    onClick: () -> Unit,
    enabled: Boolean = true
) {
    androidx.compose.material3.Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier
            .fillMaxWidth(),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (enabled) MaterialTheme.colorScheme.primary else Color.Transparent,
            contentColor = if (enabled) MaterialTheme.colorScheme.onPrimary
                else MaterialTheme.colorScheme.onSurfaceVariant,
            disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant
        ),
        border = if (enabled) BorderStroke(0.dp, Color.Transparent) else
            BorderStroke(2.dp, MaterialTheme.colorScheme.onSurfaceVariant),
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(
            text = label,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun CircleShapeButton(
    modifier: Modifier = Modifier,
    icon: IconResource,
    label: String = "Label",
    onClick: () -> Unit
) {
    Column(
        modifier = modifier
            .clip(CircleShape)
            .clickable { onClick() }
            .padding(vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        when(icon) {
            is IconResource.Drawable -> Icon(
                painter = painterResource(id = icon.resId),
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                tint = MaterialTheme.colorScheme.onSurface
            )
            is IconResource.Vector -> Icon(
                imageVector = icon.imageVector,
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                tint = MaterialTheme.colorScheme.onSurface
            )
        }
        Text(
            text = label,
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp
        )
    }
}