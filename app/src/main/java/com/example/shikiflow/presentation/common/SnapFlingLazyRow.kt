package com.example.shikiflow.presentation.common

import androidx.compose.foundation.gestures.snapping.SnapPosition
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SnapFlingLazyRow(
    modifier: Modifier = Modifier,
    snapPosition: SnapPosition = SnapPosition.Start,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
    verticalAlignment: Alignment.Vertical = Alignment.Top,
    content: LazyListScope.() -> Unit
) {
    val lazyListState = rememberLazyListState()

    LazyRow(
        state = lazyListState,
        contentPadding = contentPadding,
        flingBehavior = rememberSnapFlingBehavior(
            lazyListState = lazyListState,
            snapPosition = snapPosition
        ),
        horizontalArrangement = horizontalArrangement,
        verticalAlignment = verticalAlignment,
        modifier = modifier,
        content = content
    )
}