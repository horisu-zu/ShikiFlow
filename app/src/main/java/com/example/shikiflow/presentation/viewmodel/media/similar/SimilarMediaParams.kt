package com.example.shikiflow.presentation.viewmodel.media.similar

import com.example.shikiflow.domain.model.tracks.MediaType

data class SimilarMediaParams(
    val mediaId: Int? = null,
    val mediaType: MediaType? = null,
    val isRefreshing: Boolean = false
)