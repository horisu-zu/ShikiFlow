package com.example.shikiflow.presentation.common

import android.util.Log
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import com.example.shikiflow.utils.Converter.DescriptionElement
import com.example.shikiflow.utils.Converter.parseDescriptionHtml

@Composable
fun FormattedText(
    descriptionHtml: String,
    linkColor: Color,
    modifier: Modifier = Modifier,
    collapsedMaxLines: Int = 8,
    style: TextStyle = TextStyle.Default,
    brushColor: Color,
    onCharacterClick: (String) -> Unit,
    onLinkClick: (String) -> Unit
) {
    var isExpanded by remember { mutableStateOf(false) }
    var lineCount by remember { mutableIntStateOf(0) }
    val lineHeight = style.fontSize * 1.75f
    val shouldShowButton = lineCount >= collapsedMaxLines

    val elements = remember(descriptionHtml) {
        parseDescriptionHtml(descriptionHtml, linkColor)
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp, Alignment.Top)
    ) {
        elements.forEachIndexed { index, element ->
            when (element) {
                is DescriptionElement.Text -> {
                    AnnotatedText(
                        text = element.annotatedString,
                        collapsedMaxLines = collapsedMaxLines,
                        style = style.copy(),
                        lineHeight = lineHeight,
                        brushColor = brushColor,
                        isExpanded = isExpanded,
                        onLineCountChange = { lineCount = it },
                        onCharacterClick = {
                            Log.d("FormattedText", "Clicked on character: $it")
                            onCharacterClick(it)
                        },
                        onLinkClick = { link ->
                            Log.d("FormattedText", "Clicked on link: $link")
                            onLinkClick(link)
                        }
                    )
                }
                is DescriptionElement.Spoiler -> {
                    val isSpoilerVisible = isExpanded

                    if (isSpoilerVisible) {
                        SpoilerElement(
                            label = "Spoiler",
                            content = element.content,
                            style = style.copy(),
                            lineHeight = lineHeight,
                            brushColor = brushColor,
                            onCharacterClick = {
                                Log.d("FormattedText", "Clicked on character: $it")
                                onCharacterClick(it)
                            },
                            onLinkClick = { link ->
                                Log.d("FormattedText", "Clicked on link: $link")
                                onLinkClick(link)
                            }
                        )
                    }
                }
            }
        }

        if(shouldShowButton) {
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
}

@Composable
private fun AnnotatedText(
    text: AnnotatedString,
    collapsedMaxLines: Int,
    style: TextStyle,
    lineHeight: TextUnit,
    brushColor: Color,
    isExpanded: Boolean,
    onLineCountChange: (Int) -> Unit,
    onCharacterClick: (String) -> Unit,
    onLinkClick: (String) -> Unit
) {
    val layoutResult = remember { mutableStateOf<TextLayoutResult?>(null) }
    var lineCount by remember { mutableIntStateOf(0) }

    Text(
        text = text,
        style = style.copy(lineHeight = lineHeight),
        modifier = Modifier
            .pointerInput(Unit) {
                detectTapGestures { offset ->
                    layoutResult.value?.let { result ->
                        val position = result.getOffsetForPosition(offset)
                        Log.d("ClickDebug", "Calculated Position: $position")
                        text.getStringAnnotations("CHARACTER_ID", position, position)
                            .firstOrNull()?.let { annotation ->
                                onCharacterClick(annotation.item)
                            }

                        text.getStringAnnotations("URL_LINK", position, position)
                            .firstOrNull()?.let { annotation ->
                                onLinkClick(annotation.item)
                            }
                    }
                }
            }
            .drawWithContent {
                drawContent()
                if (!isExpanded && lineCount >= collapsedMaxLines) {
                    drawRect(
                        brush = Brush.verticalGradient(
                            colors = listOf(Color.Transparent, brushColor)
                        )
                    )
                }
            }
            .animateContentSize(
                animationSpec = tween(
                    durationMillis = 300,
                    easing = FastOutSlowInEasing
                )
            ),
        maxLines = if (isExpanded) Int.MAX_VALUE else collapsedMaxLines,
        onTextLayout = { result ->
            layoutResult.value = result
            lineCount = result.lineCount
            onLineCountChange(lineCount)
        }
    )
}

@Composable
private fun SpoilerElement(
    label: String,
    lineHeight: TextUnit,
    brushColor: Color,
    content: AnnotatedString,
    style: TextStyle,
    onCharacterClick: (String) -> Unit,
    onLinkClick: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Column(
        Modifier
            .wrapContentWidth()
            .clip(RoundedCornerShape(12.dp))
            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(12.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .animateContentSize(
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioNoBouncy,
                    stiffness = Spring.StiffnessLow
                )
            )
    ) {
        Text(
            text = label,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = {
                    expanded = !expanded
                }
            )
        )
        if (expanded) {
            AnnotatedText(
                text = content,
                collapsedMaxLines = Int.MAX_VALUE,
                style = style.copy(),
                lineHeight = lineHeight,
                brushColor = brushColor,
                isExpanded = expanded,
                onLineCountChange = { /**/ },
                onCharacterClick = { characterId ->
                    Log.d("FormattedText", "Clicked on character: $characterId")
                    onCharacterClick(characterId)
                },
                onLinkClick = { link ->
                    Log.d("FormattedText", "Clicked on link: $link")
                    onLinkClick(link)
                }
            )
        }
    }
}