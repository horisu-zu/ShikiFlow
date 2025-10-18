package com.example.shikiflow.presentation.common

import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import com.example.shikiflow.utils.IconResource
import com.example.shikiflow.utils.toIcon

@Composable
fun TextWithIcon(
    text: String,
    iconResources: List<IconResource>,
    modifier: Modifier = Modifier,
    placeIconAtTheBeginning: Boolean = true,
    style: TextStyle = MaterialTheme.typography.titleMedium,
    color: Color = MaterialTheme.colorScheme.onSurface
) {
    val textValue = buildAnnotatedString {
        when {
            placeIconAtTheBeginning -> {
                iconResources.forEachIndexed { index, _ ->
                    appendInlineContent("icon_$index")
                }
                append(text)
            }
            else -> {
                append(text)
                iconResources.forEachIndexed { index, _ ->
                    appendInlineContent("icon_$index")
                }
            }
        }
    }

    val inlineContent = iconResources.mapIndexed { index, iconResource ->
        "icon_$index" to InlineTextContent(
            placeholder = Placeholder(
                width = style.fontSize,
                height = style.fontSize,
                placeholderVerticalAlign = PlaceholderVerticalAlign.Center
            )
        ) {
            iconResource.toIcon(tint = color)
        }
    }.toMap()

    Text(
        text = textValue,
        inlineContent = inlineContent,
        modifier = modifier,
        style = style.copy(color = color)
    )
}