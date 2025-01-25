package com.example.shikiflow.utils

import androidx.annotation.DrawableRes
import androidx.compose.ui.graphics.vector.ImageVector

sealed interface IconResource {
    data class Vector(val imageVector: ImageVector): IconResource
    data class Drawable(@DrawableRes val resId: Int): IconResource
}