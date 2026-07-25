package com.example.shikiflow.presentation.common

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupPositionProvider
import androidx.compose.ui.window.PopupProperties

@Composable
fun ItemWithPopup(
    modifier: Modifier = Modifier,
    popupProperties: PopupProperties = PopupProperties(focusable = true),
    anchor: @Composable (onClick: () -> Unit) -> Unit,
    popupContent: @Composable () -> Unit
) {
    val transitionState = remember { MutableTransitionState(false) }

    val popupPositioner = remember {
        object : PopupPositionProvider {
            override fun calculatePosition(
                anchorBounds: IntRect,
                windowSize: IntSize,
                layoutDirection: LayoutDirection,
                popupContentSize: IntSize
            ): IntOffset {
                return IntOffset(
                    x = anchorBounds.left,
                    y = anchorBounds.top - popupContentSize.height
                )
            }
        }
    }

    Box(
        modifier = modifier.wrapContentSize(Alignment.TopStart)
    ) {
        anchor { transitionState.targetState = true }

        if (transitionState.currentState || transitionState.targetState) {
            Popup(
                popupPositionProvider = popupPositioner,
                onDismissRequest = { transitionState.targetState = false },
                properties = popupProperties
            ) {
                AnimatedVisibility(
                    visibleState = transitionState,
                    enter = fadeIn() + scaleIn(
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioNoBouncy,
                            stiffness = Spring.StiffnessMedium
                        ),
                        transformOrigin = TransformOrigin(0f, 1f)
                    ),
                    exit = fadeOut() + scaleOut(
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioNoBouncy,
                            stiffness = Spring.StiffnessMedium
                        ),
                        transformOrigin = TransformOrigin(0f, 1f)
                    )
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.background)
                            .padding(horizontal = 6.dp, vertical = 4.dp)
                    ) {
                        popupContent()
                    }
                }
            }
        }
    }
}