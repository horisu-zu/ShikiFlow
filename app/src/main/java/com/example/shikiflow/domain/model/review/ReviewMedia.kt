package com.example.shikiflow.domain.model.review

import com.example.shikiflow.domain.model.media_details.MediaTitle

data class ReviewMedia(
    val id: Int,
    val title: MediaTitle,
    val bannerImage: String?
)
