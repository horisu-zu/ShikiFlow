package com.example.shikiflow.presentation.common

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

enum class SheetSide {
    LEFT,
    RIGHT
}

@Composable
fun SideSheet(
    isSheetOpen: Boolean,
    onDismiss: () -> Unit,
    sheetContent: LazyListScope.() -> Unit,
    mainContent: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    sheetWidth: Dp = 300.dp,
    sheetItemSpacing: Arrangement.Vertical = Arrangement.spacedBy(6.dp, Alignment.Top),
    sheetSide: SheetSide = SheetSide.RIGHT,
    sheetCornerRadius: Dp = 24.dp,
    animationSpec: AnimationSpec<Float> = spring(
        dampingRatio = Spring.DampingRatioNoBouncy,
        stiffness = Spring.StiffnessMediumLow
    ),
    maskColor: Color = Color.Black.copy(alpha = 0.5f),
    showMask: Boolean = true,
    dragThresholdFraction: Float = 0.4f,
    enableSwipe: Boolean = true
) {
    val density = LocalDensity.current
    val scope = rememberCoroutineScope()

    val sheetWidthPx = with(density) { sheetWidth.toPx() }
    val offsetX = remember {
        Animatable(
            initialValue = if(isSheetOpen) 0f else sheetWidthPx * (if(sheetSide == SheetSide.LEFT) -1 else 1)
        )
    }

    LaunchedEffect(isSheetOpen) {
        val targetOffsetX = if(isSheetOpen) 0f else sheetWidthPx * (if(sheetSide == SheetSide.LEFT) -1 else 1)

        offsetX.animateTo(
            targetValue = targetOffsetX,
            animationSpec = animationSpec
        )
    }

    if(isSheetOpen) {
        BackHandler {
            onDismiss()
        }
    }

    Box(
        modifier = modifier.fillMaxSize()
    ) {
        mainContent()

        if(isSheetOpen && showMask) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(maskColor)
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onTap = { onDismiss() }
                        )
                    }
            )
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxHeight()
                .navigationBarsPadding()
                .systemBarsPadding()
                .width(sheetWidth)
                .offset { IntOffset(x = offsetX.value.roundToInt(), y = 0) }
                .align(
                    alignment = if(sheetSide == SheetSide.LEFT) Alignment.CenterStart
                        else Alignment.CenterEnd
                )
                .clip(
                    shape = if(sheetCornerRadius > 0.dp) {
                        when(sheetSide) {
                            SheetSide.LEFT -> RoundedCornerShape(
                                topEnd = sheetCornerRadius,
                                bottomEnd = sheetCornerRadius
                            )
                            SheetSide.RIGHT -> RoundedCornerShape(
                                topStart = sheetCornerRadius,
                                bottomStart = sheetCornerRadius
                            )
                        }
                    } else RectangleShape
                )
                .background(MaterialTheme.colorScheme.surfaceContainer)
                .pointerInput(Unit) {
                    if(enableSwipe) {
                        detectDragGestures(
                            onDragEnd = {
                                scope.launch {
                                    val shouldClose = when(sheetSide) {
                                        SheetSide.LEFT -> offsetX.value < -sheetWidthPx * dragThresholdFraction
                                        SheetSide.RIGHT -> offsetX.value > sheetWidthPx * dragThresholdFraction
                                    }

                                    val finalTarget = if(shouldClose) {
                                        sheetWidthPx * (if(sheetSide == SheetSide.LEFT) -1 else 1)
                                    } else {
                                        0f
                                    }

                                    if(shouldClose) {
                                        onDismiss()
                                    }

                                    offsetX.animateTo(
                                        targetValue = finalTarget,
                                        animationSpec = animationSpec
                                    )
                                }
                            }
                        ) { change, dragAmount ->
                            change.consume()

                            scope.launch {
                                val newOffset = offsetX.value + dragAmount.x

                                val clampedOffset = when(sheetSide) {
                                    SheetSide.LEFT -> newOffset.coerceIn(-sheetWidthPx, 0f)
                                    SheetSide.RIGHT -> newOffset.coerceIn(0f, sheetWidthPx)
                                }

                                offsetX.snapTo(clampedOffset)
                            }
                        }
                    }
                },
            contentPadding = PaddingValues(
                horizontal = sheetCornerRadius / 2,
                vertical = sheetCornerRadius / 3
            ),
            verticalArrangement = sheetItemSpacing
        ) {
            sheetContent()
        }
    }
}