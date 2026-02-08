package com.example.shikiflow.presentation.common

import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material3.LocalTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow

@Composable
fun AutoSizedText(
    text: String,
    autoSize: TextAutoSize,
    modifier: Modifier = Modifier,
    style: TextStyle = LocalTextStyle.current
) {
    BasicText(
        text = text,
        autoSize = autoSize,
        style = style,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        modifier = modifier
    )
}