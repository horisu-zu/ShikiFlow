package com.example.shikiflow.domain.model.staff

data class StaffShort(
    val id: Int,
    val fullName: StaffName,
    val imageUrl: String,
    val roles: List<StaffRole>
)
