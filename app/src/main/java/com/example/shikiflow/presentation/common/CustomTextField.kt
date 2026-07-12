package com.example.shikiflow.presentation.common

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle

@Composable
fun CustomTextField(
    textFieldState: TextFieldState,
    modifier: Modifier = Modifier,
    textStyle: TextStyle = MaterialTheme.typography.bodyLarge.copy(
        color = MaterialTheme.colorScheme.onSurface
    ),
    cursorBrush: Brush = SolidColor(MaterialTheme.colorScheme.primary),
    lineLimits: TextFieldLineLimits = TextFieldLineLimits.SingleLine,
    placeholder: @Composable () -> Unit
) {
    BasicTextField(
        state = textFieldState,
        textStyle = textStyle,
        cursorBrush = cursorBrush,
        lineLimits = lineLimits,
        decorator = { innerTextField ->
            Box {
                placeholder()

                innerTextField()
            }
        },
        modifier = modifier
    )
}