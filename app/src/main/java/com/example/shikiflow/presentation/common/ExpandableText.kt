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
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import coil3.compose.SubcomposeAsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.example.shikiflow.presentation.common.image.BaseImage
import com.example.shikiflow.presentation.common.image.ImageType
import com.example.shikiflow.presentation.common.image.RoundedImage
import com.example.shikiflow.utils.Converter.DescriptionElement
import com.example.shikiflow.utils.Converter.EntityType
import com.example.shikiflow.utils.Converter.parseDescriptionHtml
import kotlinx.coroutines.launch

@Composable
fun ExpandableText(
    descriptionHtml: String,
    modifier: Modifier = Modifier,
    onEntityClick: (EntityType, String) -> Unit,
    onLinkClick: (String) -> Unit,
    linkColor: Color = MaterialTheme.colorScheme.primary,
    collapsedMaxLines: Int = 8,
    style: TextStyle = TextStyle.Default,
    brushColor: Color = MaterialTheme.colorScheme.background.copy(0.8f)
) {
    var isExpanded by remember { mutableStateOf(false) }
    val lineHeight = style.fontSize * 1.75f

    val elements = remember(descriptionHtml) {
        parseDescriptionHtml(descriptionHtml, linkColor)
    }

    DescriptionElementsList(
        modifier = modifier,
        elements = elements,
        collapsedMaxLines = collapsedMaxLines,
        isExpanded = isExpanded,
        onExpandToggle = { isExpanded = !isExpanded },
        style = style,
        lineHeight = lineHeight,
        brushColor = brushColor,
        onEntityClick = onEntityClick,
        onLinkClick = onLinkClick
    )
}

@Composable
private fun DescriptionElementsList(
    elements: List<DescriptionElement>,
    style: TextStyle,
    lineHeight: TextUnit,
    brushColor: Color,
    onEntityClick: (EntityType, String) -> Unit,
    onLinkClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    collapsedMaxLines: Int = Int.MAX_VALUE,
    isExpanded: Boolean = true,
    onExpandToggle: () -> Unit = { }
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        elements.forEach { element ->
            when (element) {
                is DescriptionElement.Text -> {
                    AnnotatedText(
                        text = element.annotatedString,
                        collapsedMaxLines = collapsedMaxLines,
                        style = style.copy(),
                        lineHeight = lineHeight,
                        brushColor = brushColor,
                        isExpanded = isExpanded,
                        onExpandToggle = onExpandToggle,
                        onEntityClick = { entityType, id ->
                            Log.d("FormattedText", "Clicked on Entity with type $entityType: $id")
                            onEntityClick(entityType, id)
                        },
                        onLinkClick = onLinkClick
                    )
                }
                is DescriptionElement.Spoiler -> {
                    SpoilerElement(
                        label = element.label,
                        content = element.content,
                        style = style,
                        lineHeight = lineHeight,
                        brushColor = brushColor,
                        onEntityClick = onEntityClick,
                        onLinkClick = onLinkClick
                    )
                }
                is DescriptionElement.Image -> {
                    ImageItem(
                        label = element.label,
                        imageUrl = element.imageUrl,
                        onEntityClick = onEntityClick,
                        onLinkClick = onLinkClick
                    )
                }
                is DescriptionElement.Video -> {
                    VideoItem(
                        thumbnailUrl = element.thumbnailUrl,
                        onVideoClick = { onLinkClick(element.videoUrl) }
                    )
                }
                is DescriptionElement.Quote -> {
                    QuoteItem(
                        quoteElement = element,
                        style = style
                    )
                }
            }
        }
    }
}

@Composable
private fun AnnotatedText(
    text: AnnotatedString,
    style: TextStyle,
    onEntityClick: (EntityType, String) -> Unit,
    onLinkClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    collapsedMaxLines: Int = Int.MAX_VALUE,
    isExpanded: Boolean = false,
    onExpandToggle: () -> Unit = { /**/ },
    brushColor: Color = MaterialTheme.colorScheme.primary,
    lineHeight: TextUnit = TextUnit.Unspecified
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val layoutResult = remember { mutableStateOf<TextLayoutResult?>(null) }
    val textMeasurer = rememberTextMeasurer()
    val density = LocalDensity.current

    BoxWithConstraints {
        val maxWidthPx = with(density) { maxWidth.roundToPx() }

        val fullLineCount = remember(text, style, lineHeight) {
            val measuredText = textMeasurer.measure(
                text = text,
                style = style.copy(lineHeight = lineHeight),
                constraints = Constraints(maxWidth = maxWidthPx)
            )
            measuredText.lineCount
        }

        val shouldShowButton = fullLineCount > collapsedMaxLines

        Column(modifier = modifier) {
            Text(
                text = text,
                style = style.copy(lineHeight = lineHeight),
                modifier = Modifier
                    .pointerInput(Unit) {
                        detectTapGestures { offset ->
                            layoutResult.value?.let { result ->
                                val position = result.getOffsetForPosition(offset)
                                for (entityType in EntityType.entries) {
                                    text.getStringAnnotations(entityType.name, position, position)
                                        .firstOrNull()?.let { annotation ->
                                            Log.d(
                                                "Formatted Text",
                                                "Clicked on entity: ${annotation.item}"
                                            )
                                            onEntityClick(entityType, annotation.item)
                                        }
                                }

                                text.getStringAnnotations("URL_LINK", position, position)
                                    .firstOrNull()?.let { annotation ->
                                        try {
                                            Log.d(
                                                "Formatted Text",
                                                "Clicked on URL: ${annotation.item}"
                                            )
                                            onLinkClick(annotation.item)
                                        } catch (e: Exception) {
                                            Log.e(
                                                "Formatted Text",
                                                "Error opening URL: ${annotation.item}",
                                                e
                                            )
                                            scope.launch {
                                                snackbarHostState.showSnackbar("Can't open link")
                                            }
                                        }
                                    }
                            }
                        }
                    }
                    .drawWithContent {
                        drawContent()
                        if (!isExpanded && shouldShowButton) {
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
                }
            )
            if (shouldShowButton) {
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
                        ) { onExpandToggle() }
                )
            }
        }
    }
}

@Composable
private fun SpoilerElement(
    label: String,
    lineHeight: TextUnit,
    brushColor: Color,
    content: List<DescriptionElement>,
    style: TextStyle,
    onEntityClick: (EntityType, String) -> Unit,
    onLinkClick: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

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
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = { expanded = !expanded }
            )
        )
        if (expanded) {
            DescriptionElementsList(
                elements = content,
                style = style,
                lineHeight = lineHeight,
                brushColor = brushColor,
                onEntityClick = onEntityClick,
                onLinkClick = onLinkClick
            )
        }
    }
}

@Composable
private fun ImageItem(
    label: AnnotatedString,
    imageUrl: String,
    onEntityClick: (EntityType, String) -> Unit,
    onLinkClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.Start)
    ) {
        SubcomposeAsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(imageUrl)
                .memoryCacheKey(imageUrl)
                .crossfade(true)
                .build(),
            modifier = Modifier.weight(1f),
            contentScale = ContentScale.Crop,
            contentDescription = imageUrl
        )
        AnnotatedText(
            text = label,
            collapsedMaxLines = Int.MAX_VALUE,
            style = MaterialTheme.typography.labelSmall,
            onEntityClick = onEntityClick,
            onLinkClick = onLinkClick,
            isExpanded = false,
            onExpandToggle = { /**/ }
        )
    }
}

@Composable
private fun QuoteItem(
    quoteElement: DescriptionElement.Quote,
    style: TextStyle
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(4.dp))
            .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.75f))
            .padding(horizontal = 8.dp, vertical = 6.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp, Alignment.Top),
        horizontalAlignment = Alignment.Start
    ) {
        quoteElement.senderNickname?.let { nickname ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.Start),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RoundedImage(
                    model = quoteElement.senderAvatarUrl,
                    size = 24.dp,
                    clip = RoundedCornerShape(8.dp)
                )
                Text(
                    text = nickname,
                    style = style
                )
            }
        }
        Text(
            text = quoteElement.content,
            style = style
        )
    }
}

@Composable
private fun VideoItem(
    thumbnailUrl: String,
    onVideoClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.BottomEnd
    ) {
        BaseImage(
            model = thumbnailUrl,
            imageType = ImageType.Screenshot(defaultWidth = Int.MAX_VALUE.dp),
            modifier = Modifier.clickable { onVideoClick() },
            error = {
                Box(
                    modifier = Modifier.fillMaxWidth()
                        .aspectRatio(16f / 9f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surface),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null)
                }
            }
        )
    }
}