package com.example.shikiflow.domain.model.staff

data class StaffShort(
    val id: Int,
    val fullName: String,
    val imageUrl: String,
    val roles: List<String>
)
