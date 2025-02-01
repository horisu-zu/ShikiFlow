package com.example.shikiflow.presentation.common.image

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

sealed class ImageType(
    open val defaultAspectRatio: Float,
    open val defaultWidth: Dp,
    open val defaultClip: Shape
) {
    data class Poster(
        override val defaultAspectRatio: Float = 2f / 2.85f,
        override val defaultWidth: Dp = 96.dp,
        override val defaultClip: Shape = RoundedCornerShape(12.dp)
    ) : ImageType(defaultAspectRatio, defaultWidth, defaultClip)

    data class Screenshot(
        override val defaultAspectRatio: Float = 16f / 9f,
        override val defaultWidth: Dp = 280.dp,
        override val defaultClip: Shape = RoundedCornerShape(8.dp)
    ) : ImageType(defaultAspectRatio, defaultWidth, defaultClip)

    data class Square(
        override val defaultAspectRatio: Float = 1f,
        override val defaultWidth: Dp = 96.dp,
        override val defaultClip: Shape = CircleShape
    ) : ImageType(defaultAspectRatio, defaultWidth, defaultClip)

    data class Custom(
        override val defaultAspectRatio: Float,
        override val defaultWidth: Dp,
        override val defaultClip: Shape
    ) : ImageType(defaultAspectRatio, defaultWidth, defaultClip)
}