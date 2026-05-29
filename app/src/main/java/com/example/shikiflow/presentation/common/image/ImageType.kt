package com.example.shikiflow.presentation.common.image

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

sealed class ImageType(
    open val aspectRatio: Float,
    open val width: Dp,
    open val shape: Shape
) {
    data class Poster(
        override val aspectRatio: Float = 2f / 2.85f,
        override val width: Dp = 96.dp,
        override val shape: Shape = RoundedCornerShape(12.dp)
    ) : ImageType(aspectRatio, width, shape)

    data class Screenshot(
        override val aspectRatio: Float = 16f / 9f,
        override val width: Dp = 280.dp,
        override val shape: Shape = RoundedCornerShape(8.dp)
    ) : ImageType(aspectRatio, width, shape)

    data class Square(
        override val aspectRatio: Float = 1f,
        override val width: Dp = 96.dp,
        override val shape: Shape = CircleShape
    ) : ImageType(aspectRatio, width, shape)

    data class Custom(
        override val aspectRatio: Float,
        override val width: Dp,
        override val shape: Shape
    ) : ImageType(aspectRatio, width, shape)
}