package com.example.shikiflow.presentation.common

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.UriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import com.example.shikiflow.R
import com.example.shikiflow.domain.model.comment.EntityType
import com.example.shikiflow.utils.LinkRouter
import com.example.shikiflow.utils.WebIntent
import com.example.shikiflow.utils.parser_v2.ParserConfig
import com.example.shikiflow.utils.parser_v2.RichTextBlock
import com.example.shikiflow.utils.parser_v2.RichTextParser

@Composable
fun ExpandableText(
    htmlText: String,
    style: TextStyle,
    onEntityClick: (EntityType, Int) -> Unit,
    modifier: Modifier = Modifier,
    collapsedMaxLines: Int = 8,
    linkColor: Color = MaterialTheme.colorScheme.primary,
    brushColor: Color = MaterialTheme.colorScheme.background.copy(alpha = 0.8f)
) {
    val context = LocalContext.current
    val parsedText = remember(htmlText) {
        RichTextParser.parse(htmlText, ParserConfig())
    }

    var containerWidth by remember { mutableIntStateOf(0) }
    var isExpanded by remember { mutableStateOf(false) }
    val textMeasurer = rememberTextMeasurer()

    val text = buildAnnotatedString {
        parsedText.blocks.filterIsInstance<RichTextBlock.Text>()
            .forEachIndexed { index, block ->
                appendInlines(
                    inlines = block.inlines,
                    baseColor = style.color,
                    linkColor = linkColor,
                    codeBackground = style.background
                )

                if (index < parsedText.blocks.size - 1) append("\n")
            }
    }

    val fullLineCount by remember(text, style, containerWidth) {
        derivedStateOf {
            textMeasurer.measure(
                text = text,
                style = style,
                constraints = Constraints(maxWidth = containerWidth)
            ).lineCount
        }
    }

    val shouldShowButton = fullLineCount > collapsedMaxLines

    val customUriHandler = object : UriHandler {
        override fun openUri(uri: String) {
            val route = LinkRouter.getEntityData(uri)

            route?.let {
                onEntityClick(route.type, route.id.toInt())
            } ?: WebIntent.openUrlCustomTab(context, uri)
        }
    }

    CompositionLocalProvider(
        LocalUriHandler provides customUriHandler
    ) {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .onSizeChanged { size ->
                    containerWidth = size.width
                },
            verticalArrangement = Arrangement.spacedBy(4.dp, Alignment.Top)
        ) {
            Text(
                text = text,
                style = style.copy(lineHeight = style.lineHeight * 1.2),
                modifier = Modifier.drawWithContent {
                    drawContent()
                    if (!isExpanded && shouldShowButton) {
                        drawRect(
                            brush = Brush.verticalGradient(
                                colors = listOf(Color.Transparent, brushColor)
                            )
                        )
                    }
                }
                    .animateContentSize(animationSpec = MaterialTheme.motionScheme.defaultSpatialSpec()),
                maxLines = if (isExpanded) Int.MAX_VALUE else collapsedMaxLines
            )

            if (shouldShowButton) {
                Text(
                    text = stringResource(
                        id = if (isExpanded) R.string.expandable_text_collapse
                            else R.string.expandable_text_expand
                    ),
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f),
                    modifier = Modifier
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
}