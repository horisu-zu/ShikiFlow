package com.example.shikiflow.presentation.viewmodel.followings

import com.example.shikiflow.domain.model.sort.Sort
import com.example.shikiflow.domain.model.sort.SortDirection
import com.example.shikiflow.domain.model.sort.UserRateType

data class MediaFollowingsParams(
    val mediaId: Int? = null,
    val sort: Sort<UserRateType> = Sort(
        type = UserRateType.UPDATED_AT,
        direction = SortDirection.DESCENDING
    )
)
