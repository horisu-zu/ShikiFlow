package com.example.shikiflow.presentation.viewmodel.media.reviews

import com.example.shikiflow.domain.model.sort.ReviewType
import com.example.shikiflow.domain.model.sort.Sort
import com.example.shikiflow.domain.model.sort.SortDirection
import com.example.shikiflow.domain.model.tracks.MediaType

data class ReviewsParams(
    val mediaId: Int? = null,
    val mediaType: MediaType? = null,
    val sort: Sort<ReviewType> = Sort(
        type = ReviewType.RATING,
        direction = SortDirection.DESCENDING
    )
)
