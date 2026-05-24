package com.example.shikiflow.presentation.common

import android.util.Log
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.UriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.SubcomposeAsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.example.shikiflow.R
import com.example.shikiflow.domain.model.comment.EntityType
import com.example.shikiflow.presentation.common.image.BaseImage
import com.example.shikiflow.presentation.common.image.ImageType
import com.example.shikiflow.presentation.common.player.MiniPlayer
import com.example.shikiflow.utils.LinkRouter
import com.example.shikiflow.utils.WebIntent
import com.example.shikiflow.utils.parser_v2.ParsedRichText
import com.example.shikiflow.utils.parser_v2.ParserConfig
import com.example.shikiflow.utils.parser_v2.RichTextAlignment
import com.example.shikiflow.utils.parser_v2.RichTextBlock
import com.example.shikiflow.utils.parser_v2.RichTextInline
import com.example.shikiflow.utils.parser_v2.RichTextParser
import com.example.shikiflow.utils.parser_v2.RichTextTextKind
import com.example.shikiflow.utils.parser_v2.filterBlockquoteContent

@Composable
fun RichTextRenderer(
    htmlText: String,
    onEntityClick: (EntityType, Int) -> Unit,
    modifier: Modifier = Modifier,
    style: TextStyle = LocalTextStyle.current,
    linkColor: Color = MaterialTheme.colorScheme.primary
) {
    val context = LocalContext.current
    val parsedText = remember(htmlText) {
        RichTextParser.parse(htmlText, ParserConfig())
    }

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
        TextRenderer(
            parsedText = parsedText,
            style = style.copy(
                lineHeight = style.lineHeight * 1.2
            ),
            linkColor = linkColor,
            onLinkClick = { url ->
                WebIntent.openUrlCustomTab(context, url)
            },
            modifier = modifier
        )
    }
}

@Composable
private fun TextRenderer(
    parsedText: ParsedRichText,
    modifier: Modifier = Modifier,
    style: TextStyle,
    linkColor: Color,
    onLinkClick: (String) -> Unit,
    codeBackground: Color = MaterialTheme.colorScheme.surfaceContainerHighest,
    spoilerColor: Color = MaterialTheme.colorScheme.onSurfaceVariant
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(4.dp, Alignment.Top)
    ) {
        RenderBlocks(
            blocks = parsedText.blocks,
            style = style,
            linkColor = linkColor,
            codeBackground = codeBackground,
            spoilerColor = spoilerColor,
            onLinkClick = onLinkClick
        )
    }
}

@Composable
private fun RenderBlocks(
    blocks: List<RichTextBlock>,
    style: TextStyle,
    linkColor: Color,
    codeBackground: Color,
    spoilerColor: Color,
    onLinkClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    blocks.forEach { block ->
        val blockAlignment = when(block.align.toTextAlign()) {
            TextAlign.Center -> Alignment.CenterHorizontally
            TextAlign.Right -> Alignment.End
            else -> Alignment.Start
        }

        Column(
            modifier = modifier.fillMaxWidth(),
            horizontalAlignment = blockAlignment,
            verticalArrangement = Arrangement.spacedBy(4.dp, Alignment.Top)
        ) {
            when(block) {
                is RichTextBlock.Text -> {
                    RichText(
                        block = block,
                        style = style,
                        linkColor = linkColor,
                        codeBackground = codeBackground
                    )
                }
                is RichTextBlock.Image -> {
                    RichImage(
                        image = block,
                        onLinkClick = onLinkClick
                    )
                }
                is RichTextBlock.Video -> {
                    MiniPlayer(
                        videoUrl = block.url,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                is RichTextBlock.Spoiler -> {
                    RichSpoiler(
                        block = block,
                        style = style,
                        linkColor = linkColor,
                        codeBackground = codeBackground,
                        spoilerColor = spoilerColor,
                        onLinkClick = onLinkClick
                    )
                }
                is RichTextBlock.Blockquote -> {
                    RichQuoteItem(
                        block = block,
                        style = style,
                        linkColor = linkColor,
                        codeBackground = codeBackground,
                        spoilerColor = spoilerColor,
                        onLinkClick = onLinkClick
                    )
                }
                is RichTextBlock.CodeBlock -> {
                    RichCodeItem(
                        block = block,
                        style = style,
                        codeBackground = codeBackground
                    )
                }
                is RichTextBlock.HorizontalRule -> {
                    HorizontalRuleItem(
                        block = block,
                        spoilerColor = spoilerColor
                    )
                }
                is RichTextBlock.InlineGroup -> {
                    RichGroupItem(
                        block = block,
                        style = style,
                        linkColor = linkColor,
                        codeBackground = codeBackground,
                        onLinkClick = onLinkClick
                    )
                }
                is RichTextBlock.ListBlock -> {
                    RichListItem(
                        block = block,
                        style = style,
                        linkColor = linkColor,
                        codeBackground = codeBackground,
                        spoilerColor = spoilerColor,
                        onLinkClick = onLinkClick
                    )
                }
                is RichTextBlock.Table -> {
                    RichTableItem(
                        block = block,
                        style = style,
                        linkColor = linkColor,
                        codeBackground = codeBackground,
                        spoilerColor = spoilerColor,
                        onLinkClick = onLinkClick
                    )
                }
                is RichTextBlock.YouTube -> {
                    YoutubeItem(
                        block = block,
                        onLinkClick = onLinkClick
                    )
                }
                is RichTextBlock.Link -> Unit
            }
        }
    }
}

@Composable
private fun RichText(
    block: RichTextBlock.Text,
    style: TextStyle,
    linkColor: Color,
    codeBackground: Color,
    modifier: Modifier = Modifier
) {
    Text(
        text = block.inlines.toAnnotatedString(
            baseColor = style.color,
            linkColor = linkColor,
            codeBackground = codeBackground,
            headingKind = block.kind
        ),
        style = style.copy(
            textAlign = block.align.toTextAlign()
        ),
        modifier = modifier
    )
}

@Composable
private fun RichImage(
    image: RichTextBlock.Image,
    onLinkClick: (String) -> Unit,
    fillWidth: Boolean = false
) {
    val modifier = when {
        image.isPercent && image.width != null -> Modifier.fillMaxWidth(image.width / 100f)
        fillWidth || image.width != null -> Modifier.fillMaxWidth()
        else -> Modifier
    }

    val placeholderAspectRatio = if (image.width != null && image.height != null && image.height > 0) {
        image.width.toFloat() / image.height.toFloat()
    } else {
        null
    }

    SubcomposeAsyncImage(
        model = ImageRequest.Builder(LocalContext.current)
            .data(image.url)
            .memoryCacheKey(image.url)
            .crossfade(true)
            .build(),
        contentDescription = null,
        loading = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .then(
                        placeholderAspectRatio?.let {
                            Modifier.aspectRatio(placeholderAspectRatio)
                        } ?: Modifier.heightIn(min = 160.dp)
                    )
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .shimmerEffect()
            )
        },
        error = { errorState ->
            Log.d("ImageItem", "Error Loading Image", errorState.result.throwable)

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .then(
                        placeholderAspectRatio?.let {
                            Modifier.aspectRatio(placeholderAspectRatio)
                        } ?: Modifier.heightIn(min = 160.dp)
                    )
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.surfaceContainer)
                    .clickable { onLinkClick(image.url) },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(R.string.error_image)
                )
            }
        },
        contentScale = if (fillWidth || image.width != null) ContentScale.FillWidth
            else ContentScale.Inside,
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable {
                onLinkClick(image.url)
            }
    )
}

@Composable
private fun RichSpoiler(
    block: RichTextBlock.Spoiler,
    style: TextStyle,
    linkColor: Color,
    codeBackground: Color,
    spoilerColor: Color,
    onLinkClick: (String) -> Unit
) {
    var isExpanded by remember { mutableStateOf(false) }

    Column(
        Modifier
            .wrapContentWidth()
            .clip(RoundedCornerShape(8.dp))
            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(8.dp))
            .padding(horizontal = 8.dp, vertical = 6.dp)
            .animateContentSize(
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioNoBouncy,
                    stiffness = Spring.StiffnessLow
                )
            ),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = block.label ?: stringResource(R.string.spoiler_label),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = { isExpanded = !isExpanded }
            )
        )

        if(isExpanded) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                RenderBlocks(
                    blocks = block.children,
                    style = style,
                    linkColor = linkColor,
                    codeBackground = codeBackground,
                    spoilerColor = spoilerColor,
                    onLinkClick = onLinkClick
                )
            }
        }
    }
}

@Composable
private fun RichQuoteItem(
    block: RichTextBlock.Blockquote,
    style: TextStyle,
    linkColor: Color,
    codeBackground: Color,
    spoilerColor: Color,
    onLinkClick: (String) -> Unit
) {
    val onBackgroundColor = MaterialTheme.colorScheme.onBackground

    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .background(MaterialTheme.colorScheme.surfaceContainerLow)
            .drawBehind {
                drawRect(
                    color = onBackgroundColor,
                    size = Size(4.dp.toPx(), size.height)
                )
            }
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp, Alignment.Top)
    ) {
        block.senderNickname?.let { nickname ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.Start),
                verticalAlignment = Alignment.CenterVertically
            ) {
                BaseImage(
                    model = block.senderAvatarUrl,
                    imageType = ImageType.Square(
                        width = 24.dp,
                        clip = RoundedCornerShape(8.dp)
                    )
                )
                Text(
                    text = nickname,
                    style = style
                )
            }
        }

        RenderBlocks(
            blocks = filterBlockquoteContent(block),
            style = style,
            linkColor = linkColor,
            codeBackground = codeBackground,
            spoilerColor = spoilerColor,
            onLinkClick = onLinkClick
        )
    }
}

@Composable
private fun RichCodeItem(
    block: RichTextBlock.CodeBlock,
    style: TextStyle,
    codeBackground: Color
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(codeBackground)
            .horizontalScroll(rememberScrollState())
            .padding(12.dp)
    ) {
        Text(
            text = block.code,
            style = style.copy(
                fontFamily = FontFamily.Monospace,
                fontSize = 13.sp
            )
        )
    }
}

@Composable
private fun HorizontalRuleItem(
    block: RichTextBlock.HorizontalRule,
    spoilerColor: Color
) {
    HorizontalDivider(
        modifier = Modifier
            .then(
                if (block.widthPercent != null) {
                    Modifier.fillMaxWidth(block.widthPercent / 100f)
                } else {
                    Modifier.fillMaxWidth()
                }
            )
            .padding(vertical = 4.dp),
        color = spoilerColor.copy(alpha = 0.3f),
        thickness = 1.dp
    )
}

@Composable
private fun YoutubeItem(
    block: RichTextBlock.YouTube,
    onLinkClick: (String) -> Unit
) {
    val videoId = remember(block.videoIdOrUrl) {
        extractYouTubeVideoId(block.videoIdOrUrl)
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .clickable { onLinkClick("https://www.youtube.com/watch?v=$videoId") }
    ) {
        SubcomposeAsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data("https://img.youtube.com/vi/$videoId/hqdefault.jpg")
                .crossfade(true)
                .build(),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            loading = {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(16f / 9f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.surfaceContainerHighest)
                        .shimmerEffect()
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(16f / 9f)
                .background(MaterialTheme.colorScheme.surfaceContainerHighest)
        )

        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .background(
                    color = MaterialTheme.colorScheme.background.copy(alpha = 0.75f),
                    shape = CircleShape
                )
                .padding(all = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Rounded.PlayArrow,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(32.dp)
            )
        }
    }
}

@Composable
private fun RichTableItem(
    block: RichTextBlock.Table,
    style: TextStyle,
    linkColor: Color,
    codeBackground: Color,
    spoilerColor: Color,
    onLinkClick: (String) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surfaceContainerLowest)
            .drawBehind {
                drawRoundRect(
                    color = spoilerColor.copy(alpha = 0.2f),
                    size = size,
                    style = Stroke(1.dp.toPx())
                )
            }
    ) {
        block.rows.forEachIndexed { rowIndex, row ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .drawBehind {
                        if (rowIndex < block.rows.size - 1) {
                            drawLine(
                                color = spoilerColor.copy(alpha = 0.2f),
                                start = Offset(0f, size.height),
                                end = Offset(size.width, size.height),
                                strokeWidth = 1.dp.toPx()
                            )
                        }
                    }
            ) {
                row.cells.forEachIndexed { cellIndex, cell ->
                    val cellHorizontalAlignment = when (cell.align) {
                        RichTextAlignment.Center -> Alignment.Center
                        RichTextAlignment.End -> Alignment.CenterEnd
                        else -> Alignment.CenterStart
                    }
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .drawBehind {
                                if (cellIndex < row.cells.size - 1) {
                                    drawLine(
                                        color = spoilerColor.copy(alpha = 0.2f),
                                        start = Offset(size.width, 0f),
                                        end = Offset(size.width, size.height),
                                        strokeWidth = 1.dp.toPx()
                                    )
                                }
                            }
                            .background(
                                if (cell.isHeader) spoilerColor.copy(alpha = 0.05f)
                                else Color.Transparent
                            )
                            .padding(8.dp),
                        contentAlignment = cellHorizontalAlignment
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            RenderBlocks(
                                blocks = cell.children,
                                style = style.copy(
                                    fontWeight = if (cell.isHeader) FontWeight.Bold else FontWeight.Normal
                                ),
                                linkColor = linkColor,
                                codeBackground = codeBackground,
                                spoilerColor = spoilerColor,
                                onLinkClick = onLinkClick
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun RichListItem(
    block: RichTextBlock.ListBlock,
    style: TextStyle,
    linkColor: Color,
    codeBackground: Color,
    spoilerColor: Color,
    onLinkClick: (String) -> Unit,
) {
    Column(
        modifier = Modifier.padding(start = 12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        block.items.forEach { item ->
            Row(modifier = Modifier.fillMaxWidth()) {
                if (item.bullet != null) {
                    Text(
                        text = item.bullet,
                        style = style.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        modifier = Modifier.padding(end = 8.dp)
                    )
                }

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    RenderBlocks(
                        blocks = item.children,
                        style = style,
                        linkColor = linkColor,
                        codeBackground = codeBackground,
                        spoilerColor = spoilerColor,
                        onLinkClick = onLinkClick
                    )
                }
            }
        }
    }
}

@Composable
private fun RichGroupItem(
    block: RichTextBlock.InlineGroup,
    style: TextStyle,
    linkColor: Color,
    codeBackground: Color,
    onLinkClick: (String) -> Unit,
) {
    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = when (block.align.toTextAlign()) {
            TextAlign.Center -> Arrangement.spacedBy(
                8.dp,
                Alignment.CenterHorizontally
            )

            TextAlign.Right -> Arrangement.spacedBy(8.dp, Alignment.End)
            else -> Arrangement.spacedBy(8.dp, Alignment.Start)
        },
        verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically)
    ) {
        block.children.forEach { child ->
            when (child) {
                is RichTextBlock.Text -> {
                    Text(
                        text = child.inlines.toAnnotatedString(
                            baseColor = style.color,
                            linkColor = linkColor,
                            codeBackground = codeBackground,
                            headingKind = child.kind
                        ),
                        style = style.copy(color = style.color),
                        textAlign = child.align.toTextAlign(),
                        modifier = Modifier.align(Alignment.CenterVertically)
                    )
                }
                is RichTextBlock.Image -> {
                    RichImage(child, onLinkClick)
                }
                /*is RichTextBlock.Link -> {
                    LinkCard(
                        block = child,
                        style = style,
                        onLinkClick = onLinkClick
                    )
                }*/
                else -> Unit
            }
        }
    }
}

/*@Composable
fun LinkCard(
    block: RichTextBlock.Link,
    style: TextStyle,
    onLinkClick: (String) -> Unit
) {

}*/

private fun RichTextAlignment.toTextAlign(): TextAlign = when (this) {
    RichTextAlignment.Start -> TextAlign.Start
    RichTextAlignment.Center -> TextAlign.Center
    RichTextAlignment.End -> TextAlign.Right
    RichTextAlignment.Justify -> TextAlign.Justify
}

private fun List<RichTextInline>.toAnnotatedString(
    baseColor: Color,
    linkColor: Color,
    codeBackground: Color,
    headingKind: RichTextTextKind
): AnnotatedString = buildAnnotatedString {
    withStyle(headingKind.toSpanStyle()) {
        appendInlines(
            inlines = this@toAnnotatedString,
            baseColor = baseColor,
            linkColor = linkColor,
            codeBackground = codeBackground
        )
    }
}

fun AnnotatedString.Builder.appendInlines(
    inlines: List<RichTextInline>,
    baseColor: Color,
    linkColor: Color,
    codeBackground: Color
) {
    for (inline in inlines) {
        when (inline) {
            is RichTextInline.Text -> append(inline.value)
            is RichTextInline.LineBreak -> append("\n")
            is RichTextInline.Bold -> withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                appendInlines(inline.children, baseColor, linkColor, codeBackground)
            }

            is RichTextInline.Italic -> withStyle(SpanStyle(fontStyle = FontStyle.Italic)) {
                appendInlines(inline.children, baseColor, linkColor, codeBackground)
            }

            is RichTextInline.BoldItalic -> withStyle(
                SpanStyle(
                    fontWeight = FontWeight.Bold,
                    fontStyle = FontStyle.Italic
                )
            ) {
                appendInlines(inline.children, baseColor, linkColor, codeBackground)
            }

            is RichTextInline.Strikethrough -> withStyle(
                SpanStyle(textDecoration = TextDecoration.LineThrough)
            ) {
                appendInlines(inline.children, baseColor, linkColor, codeBackground)
            }

            is RichTextInline.Link -> {
                pushLink(
                    LinkAnnotation.Url(
                        inline.url,
                        TextLinkStyles(style = SpanStyle(color = linkColor))
                    )
                )
                appendInlines(inline.children, baseColor, linkColor, codeBackground)
                pop()
            }

            is RichTextInline.InlineCode -> {
                withStyle(
                    SpanStyle(
                        fontFamily = FontFamily.Monospace,
                        background = codeBackground,
                        color = if (codeBackground.luminance() > 0.5f) Color.Black else Color.White,
                        fontSize = 13.sp
                    )
                ) {
                    append(inline.code)
                }
            }
        }
    }
}

private fun RichTextTextKind.toSpanStyle(): SpanStyle = when (this) {
    RichTextTextKind.Paragraph -> SpanStyle()
    RichTextTextKind.Heading1 -> SpanStyle(fontSize = 28.sp, fontWeight = FontWeight.ExtraBold)
    RichTextTextKind.Heading2 -> SpanStyle(fontSize = 24.sp, fontWeight = FontWeight.Bold)
    RichTextTextKind.Heading3 -> SpanStyle(fontSize = 20.sp, fontWeight = FontWeight.Bold)
    RichTextTextKind.Heading4 -> SpanStyle(fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
    RichTextTextKind.Heading5 -> SpanStyle(fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
}

private fun extractYouTubeVideoId(value: String): String {
    var id = value
    while (id.contains("v=") || id.contains("youtu.be/") || id.contains("embed/")) {
        id = when {
            id.contains("v=") -> id.substringAfterLast("v=")
            id.contains("youtu.be/") -> id.substringAfterLast("youtu.be/")
            id.contains("embed/") -> id.substringAfterLast("embed/")
            else -> id
        }
    }
    id = id.substringBefore("&").substringBefore("?")
    val clean = id.replace(Regex("[^a-zA-Z0-9_-]"), "")
    return if (clean.length >= 11) clean.takeLast(11) else clean
}