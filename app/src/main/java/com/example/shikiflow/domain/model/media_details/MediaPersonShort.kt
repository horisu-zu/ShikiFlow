package com.example.shikiflow.domain.model.media_details

import com.example.shikiflow.domain.model.staff.StaffName

data class MediaPersonShort(
    val id: Int,
    val fullName: StaffName,
    val imageUrl: String
)
