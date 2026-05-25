package com.example.shikiflow.presentation.viewmodel.anime.tracks.search

import com.example.shikiflow.domain.model.sort.Sort
import com.example.shikiflow.domain.model.sort.SortDirection
import com.example.shikiflow.domain.model.sort.UserRateType
import com.example.shikiflow.domain.model.track.UserRateStatus
import com.example.shikiflow.domain.model.tracks.MediaType

data class TracksParams(
    val query: String = "",
    val userRateStatus: UserRateStatus? = null,
    val mediaType: MediaType? = null,
    val sort: Sort<UserRateType> = Sort(
        type = UserRateType.UPDATED_AT,
        direction = SortDirection.DESCENDING
    )
)