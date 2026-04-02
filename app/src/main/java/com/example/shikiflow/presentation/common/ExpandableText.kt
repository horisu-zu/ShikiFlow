package com.example.shikiflow.presentation.common

import android.util.Log
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import coil3.compose.SubcomposeAsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.example.shikiflow.R
import com.example.shikiflow.domain.model.auth.AuthType
import com.example.shikiflow.domain.model.comment.DescriptionElement
import com.example.shikiflow.domain.model.comment.EntityType
import com.example.shikiflow.presentation.common.image.BaseImage
import com.example.shikiflow.presentation.common.image.ImageType
import com.example.shikiflow.presentation.common.image.RoundedImage
import com.example.shikiflow.presentation.common.image.shimmerEffect
import com.example.shikiflow.utils.parser.AnilistDialect
import com.example.shikiflow.utils.parser.HTMLParser
import com.example.shikiflow.utils.parser.ShikimoriDialect

@Composable
fun ExpandableText(
    htmlText: String,
    authType: AuthType?,
    modifier: Modifier = Modifier,
    onEntityClick: (EntityType, Int) -> Unit,
    onLinkClick: (String) -> Unit,
    linkColor: Color = MaterialTheme.colorScheme.primary,
    collapsedMaxLines: Int = 8,
    style: TextStyle = LocalTextStyle.current,
    brushColor: Color = MaterialTheme.colorScheme.background.copy(0.8f)
) {
    val parser = remember(authType) {
        authType?.let {
            HTMLParser(
                strategy = when(authType) {
                    AuthType.SHIKIMORI -> ShikimoriDialect()
                    AuthType.ANILIST -> AnilistDialect()
                }
            )
        }
    }
    val elements = remember(htmlText) {
        parser?.parseHtmlString(htmlText, linkColor)
    }

    elements?.let { descriptionElements ->
        DescriptionElementsList(
            modifier = modifier,
            elements = descriptionElements,
            collapsedMaxLines = collapsedMaxLines,
            style = style,
            brushColor = brushColor,
            onEntityClick = onEntityClick,
            onLinkClick = onLinkClick
        )
    }
}

@Composable
fun DescriptionElementsList(
    elements: List<DescriptionElement>,
    style: TextStyle,
    brushColor: Color,
    onEntityClick: (EntityType, Int) -> Unit,
    onLinkClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    collapsedMaxLines: Int = Int.MAX_VALUE
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
                        brushColor = brushColor,
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
                        brushColor = brushColor,
                        onEntityClick = onEntityClick,
                        onLinkClick = onLinkClick
                    )
                }
                is DescriptionElement.Image -> {
                    ImageItem(
                        imageData = element,
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

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun AnnotatedText(
    text: AnnotatedString,
    style: TextStyle,
    onEntityClick: (EntityType, Int) -> Unit,
    onLinkClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    collapsedMaxLines: Int = Int.MAX_VALUE,
    brushColor: Color = MaterialTheme.colorScheme.primary
) {
    val layoutResult = remember { mutableStateOf<TextLayoutResult?>(null) }
    var containerWidth by remember { mutableIntStateOf(0) }
    var isExpanded by remember { mutableStateOf(false) }
    val textMeasurer = rememberTextMeasurer()

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

    Column(
        modifier = modifier.onSizeChanged { size ->
            containerWidth = size.width
        },
        verticalArrangement = Arrangement.spacedBy(4.dp, Alignment.Top)
    ) {
        Text(
            text = text,
            style = style.copy(lineHeight = style.lineHeight * 1.2),
            modifier = Modifier
                .pointerInput(Unit) {
                    detectTapGestures { offset ->
                        layoutResult.value?.let { result ->
                            val position = result.getOffsetForPosition(offset)
                            Log.d("FormattedText", "Clicked on position: $position")
                            for (entityType in EntityType.entries) {
                                val allAnnotations =
                                    text.getStringAnnotations(entityType.name, 0, text.length)
                                Log.d(
                                    "FormattedText",
                                    "All ${entityType.name} annotations: ${allAnnotations.map { "${it.start}-${it.end}: ${it.item}" }}"
                                )
                            }

                            for (entityType in EntityType.entries) {
                                text.getStringAnnotations(entityType.name, position, position)
                                    .firstOrNull()?.let { annotation ->
                                        Log.d(
                                            "Formatted Text",
                                            "Clicked on entity: ${annotation.item}"
                                        )
                                        onEntityClick(entityType, annotation.item.toInt())
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
                    animationSpec = MaterialTheme.motionScheme.defaultSpatialSpec()
                ),
            maxLines = if (isExpanded) Int.MAX_VALUE else collapsedMaxLines,
            onTextLayout = { result ->
                layoutResult.value = result
            }
        )
        if (shouldShowButton) {
            Text(
                text = stringResource(
                    id = if(isExpanded) R.string.expandable_text_collapse
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

@Composable
private fun SpoilerElement(
    label: String?,
    brushColor: Color,
    content: List<DescriptionElement>,
    style: TextStyle,
    onEntityClick: (EntityType, Int) -> Unit,
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
            text = label ?: stringResource(R.string.spoiler_label),
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
                brushColor = brushColor,
                onEntityClick = onEntityClick,
                onLinkClick = onLinkClick
            )
        }
    }
}

@Composable
private fun ImageItem(
    imageData: DescriptionElement.Image,
    onLinkClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    SubcomposeAsyncImage(
        model = ImageRequest.Builder(LocalContext.current)
            .data(imageData.imageUrl)
            .memoryCacheKey(imageData.imageUrl)
            .crossfade(true)
            .build(),
        loading = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(imageData.aspectRatio)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .shimmerEffect()
            )
        },
        error = { errorState ->
            Log.d("ImageItem", "Error Loading Image", errorState.result.throwable)
            val isImgur = imageData.imageUrl?.contains("imgur.com") == true

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(imageData.aspectRatio)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.surfaceContainer)
                    .clickable {
                        imageData.imageUrl?.let { imageUrl ->
                            onLinkClick(imageUrl)
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if(isImgur) stringResource(R.string.error_imgur_image)
                        else stringResource(R.string.error_image)
                )
            }
        },
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .clickable {
                imageData.imageUrl?.let { imageUrl ->
                    onLinkClick(imageUrl)
                }
            },
        contentScale = ContentScale.Crop,
        contentDescription = imageData.imageUrl
    )
}

@Composable
private fun QuoteItem(
    quoteElement: DescriptionElement.Quote,
    style: TextStyle,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.height(IntrinsicSize.Min)
            .clip(RoundedCornerShape(4.dp))
            .background(MaterialTheme.colorScheme.surfaceContainerLow)
    ) {
        VerticalDivider(
            modifier = Modifier.fillMaxHeight(),
            thickness = 4.dp,
            color = MaterialTheme.colorScheme.onBackground
        )
        Column(
            modifier = Modifier.weight(1f)
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
                        modifier = Modifier.size(24.dp),
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
}

@Composable
private fun VideoItem(
    thumbnailUrl: String?,
    onVideoClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val imageType = ImageType.Screenshot(
        defaultWidth = Int.MAX_VALUE.dp
    )

    Box(
        modifier = modifier.fillMaxWidth()
            .clip(imageType.defaultClip)
            .clickable { onVideoClick() },
        contentAlignment = Alignment.Center
    ) {
        BaseImage(
            model = thumbnailUrl,
            imageType = imageType,
            error = {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(16f / 9f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surfaceContainer)
                )
            }
        )
        Box(
            modifier = Modifier
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.background.copy(alpha = 0.75f))
                .padding(all = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.PlayArrow,
                contentDescription = null,
                modifier = Modifier.size(32.dp)
            )
        }
    }
}