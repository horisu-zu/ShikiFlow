package com.example.shikiflow.presentation.viewmodel.anime.tracks.search

import com.example.shikiflow.domain.model.auth.AuthType
import com.example.shikiflow.domain.model.media_details.Genre
import com.example.shikiflow.domain.model.sort.Sort
import com.example.shikiflow.domain.model.sort.SortDirection
import com.example.shikiflow.domain.model.sort.UserRateType
import com.example.shikiflow.domain.model.track.UserRateStatus
import com.example.shikiflow.domain.model.tracks.MediaType
import com.example.shikiflow.presentation.screen.main.TracksFilterType

data class TracksParams(
    val authType: AuthType? = null,
    val query: String = "",
    val userRateStatus: UserRateStatus? = null,
    val mediaType: MediaType? = null,
    val sort: Sort<UserRateType> = Sort(
        type = UserRateType.UPDATED_AT,
        direction = SortDirection.DESCENDING
    ),
    val currentFilterType: TracksFilterType = TracksFilterType.SORT,
    val genres: List<Genre> = emptyList()
)