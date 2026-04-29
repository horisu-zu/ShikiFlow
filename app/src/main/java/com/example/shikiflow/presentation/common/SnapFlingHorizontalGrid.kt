package com.example.shikiflow.presentation.common

import androidx.compose.foundation.gestures.snapping.SnapPosition
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SnapFlingHorizontalGrid(
    modifier: Modifier = Modifier,
    rows: GridCells = GridCells.Fixed(2),
    snapPosition: SnapPosition = SnapPosition.Start,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
    verticalArrangement: Arrangement.Vertical = Arrangement.spacedBy(8.dp, Alignment.Top),
    content: LazyGridScope.() -> Unit
) {
    val lazyGridState = rememberLazyGridState()

    LazyHorizontalGrid(
        state = lazyGridState,
        rows = rows,
        contentPadding = contentPadding,
        flingBehavior = rememberSnapFlingBehavior(
            lazyGridState = lazyGridState,
            snapPosition = snapPosition
        ),
        horizontalArrangement = horizontalArrangement,
        verticalArrangement = verticalArrangement,
        modifier = modifier,
        content = content
    )
}