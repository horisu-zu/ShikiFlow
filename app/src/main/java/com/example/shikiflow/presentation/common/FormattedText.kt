package com.example.shikiflow.presentation.common

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.shikiflow.utils.Converter

@Composable
fun FormattedText(
    text: String,
    linkColor: Color,
    modifier: Modifier = Modifier,
    collapsedMaxLines: Int = 8,
    style: TextStyle = TextStyle.Default,
    brushColor: Color,
    onClick: (id: String) -> Unit
) {
    var isExpanded by remember { mutableStateOf(false) }
    val annotatedString = Converter.formatText(text, linkColor)

    val layoutResult = remember { mutableStateOf<TextLayoutResult?>(null) }
    val lineHeight = style.fontSize * 1.75f

    Column(
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .clip(RectangleShape)
                .heightIn(max = if (isExpanded) Dp.Unspecified else with(LocalDensity.current) {
                    lineHeight.toDp() * collapsedMaxLines
                })
                .animateContentSize(
                    animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing)
                )
        ) {
            Text(
                text = annotatedString,
                style = style.copy(
                    lineHeight = lineHeight
                ),
                modifier = Modifier
                    .pointerInput(Unit) {
                        detectTapGestures { offset ->
                            layoutResult.value?.let { result ->
                                val position = result.getOffsetForPosition(offset)
                                annotatedString.getStringAnnotations("CHARACTER_ID", position, position)
                                    .firstOrNull()
                                    ?.let { annotation ->
                                        onClick(annotation.item)
                                    }
                            }
                        }
                    }
                    .drawWithContent {
                        drawContent()
                        if (!isExpanded) {
                            drawRect(
                                brush = Brush.verticalGradient(
                                    colors = listOf(Color.Transparent, brushColor)
                                )
                            )
                        }
                    },
                maxLines = if (isExpanded) Int.MAX_VALUE else collapsedMaxLines,
                onTextLayout = { layoutResult.value = it }
            )
        }

        Text(
            text = if (isExpanded) "Collapse" else "Expand",
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.SemiBold
            ),
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            modifier = Modifier
                .padding(vertical = 4.dp)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {
                    isExpanded = !isExpanded
                }
        )
    }
}