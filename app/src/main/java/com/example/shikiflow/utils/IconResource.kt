package com.example.shikiflow.utils

import androidx.annotation.DrawableRes
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource

sealed interface IconResource {
    data class Vector(val imageVector: ImageVector): IconResource
    data class Drawable(@DrawableRes val resId: Int): IconResource
}

@Composable
fun IconResource.toIcon(
    modifier: Modifier = Modifier,
    tint: Color = MaterialTheme.colorScheme.surface
) = when (this) {
    is IconResource.Drawable -> Icon(
        painter = painterResource(id = resId),
        contentDescription = null,
        modifier = modifier,
        tint = tint
    )
    is IconResource.Vector -> Icon(
        imageVector = imageVector,
        contentDescription = null,
        modifier = modifier,
        tint = tint
    )
}