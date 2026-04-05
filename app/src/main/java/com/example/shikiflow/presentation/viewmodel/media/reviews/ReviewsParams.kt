package com.example.shikiflow.presentation.viewmodel.media.reviews

import com.example.shikiflow.domain.model.tracks.MediaType

data class ReviewsParams(
    val mediaId: Int? = null,
    val mediaType: MediaType? = null
)
