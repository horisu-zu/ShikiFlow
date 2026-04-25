package com.example.shikiflow.presentation.common

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.style.ExperimentalFoundationStyleApi
import androidx.compose.foundation.style.MutableStyleState
import androidx.compose.foundation.style.styleable
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.shikiflow.utils.IconResource
import com.example.shikiflow.utils.toIcon

@OptIn(ExperimentalFoundationStyleApi::class)
@Composable
fun Button(
    modifier: Modifier = Modifier,
    icon: IconResource? = null,
    label: String = "Confirm operation",
    containerColor: Color = MaterialTheme.colorScheme.primary.copy(alpha = 0.75f),
    onClick: () -> Unit,
    enabled: Boolean = true
) {
    val styleState = remember { MutableStyleState(null) }

    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier,
        colors = ButtonDefaults.buttonColors(
            containerColor = if (enabled) containerColor else Color.Transparent,
            contentColor = if (enabled) Color.White
                else MaterialTheme.colorScheme.onSurfaceVariant,
            disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant
        ),
        border = if (enabled) BorderStroke(0.dp, Color.Transparent) else
            BorderStroke(2.dp, MaterialTheme.colorScheme.onSurfaceVariant),
        shape = RoundedCornerShape(8.dp)
    ) {
        icon?.let {
            icon.toIcon(
                modifier = Modifier.styleable(styleState) {
                    size(24.dp)
                }
            )
            Spacer(
                Modifier.styleable(styleState) {
                    size(ButtonDefaults.IconSpacing)
                }
            )
        }
        Text(
            text = label,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center
        )
    }
}