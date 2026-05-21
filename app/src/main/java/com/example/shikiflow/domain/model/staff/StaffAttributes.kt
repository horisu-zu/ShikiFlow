package com.example.shikiflow.domain.model.staff

import com.example.shikiflow.domain.model.track.Date

data class StaffAttributes(
    val dateOfBirth: Date?,
    val dateOfDeath: Date?,
    val age: String?,
    val gender: Gender?,
    val yearsActive: List<Int>?,
    val hometown: String?
)

enum class Gender {
    MALE,
    FEMALE,
    OTHER
}
