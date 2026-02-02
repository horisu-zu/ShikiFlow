package com.example.shikiflow.domain.model.staff

import com.example.shikiflow.domain.model.track.UserRateStatus

data class StaffMediaRole(
    val id: Int,
    val title: String,
    val coverImageUrl: String?,
    val staffRoles: List<String>,
    val userRateStatus: UserRateStatus? = null
)
