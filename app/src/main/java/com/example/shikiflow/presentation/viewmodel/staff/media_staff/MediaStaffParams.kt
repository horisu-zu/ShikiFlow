package com.example.shikiflow.presentation.viewmodel.staff.media_staff

import com.example.shikiflow.domain.model.sort.Sort
import com.example.shikiflow.domain.model.sort.SortDirection
import com.example.shikiflow.domain.model.sort.StaffType
import com.example.shikiflow.domain.model.tracks.MediaType

data class MediaStaffParams(
    val mediaId: Int? = null,
    val mediaType: MediaType = MediaType.ANIME,
    val staffSort: Sort<StaffType> = Sort(type = StaffType.RELEVANCE, direction = SortDirection.DESCENDING)
)