package com.example.shikiflow.domain.model.comment

import androidx.compose.ui.text.AnnotatedString

sealed class DescriptionElement {
    data class Text(val annotatedString: AnnotatedString) : DescriptionElement()
    data class Spoiler(val label: String?, val content: List<DescriptionElement>) : DescriptionElement()
    data class Image(val imageUrl: String?, val aspectRatio: Float) : DescriptionElement()
    data class Video(val videoUrl: String, val thumbnailUrl: String?) : DescriptionElement()
    data class Quote(
        val senderAvatarUrl: String?,
        val senderNickname: String?,
        val content: String
    ) : DescriptionElement()
}
