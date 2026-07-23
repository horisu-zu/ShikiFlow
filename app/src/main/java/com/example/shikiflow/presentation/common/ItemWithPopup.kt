package com.example.shikiflow.presentation.common

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
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
    var expanded by remember { mutableStateOf(false) }

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
        anchor { expanded = true }

        if (expanded) {
            Popup(
                popupPositionProvider = popupPositioner,
                onDismissRequest = { expanded = false },
                properties = popupProperties
            ) {
                popupContent()
            }
        }
    }
}