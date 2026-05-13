package com.example.shikiflow.domain.model.staff

import com.example.shikiflow.domain.model.media_details.PreferredTitleType

data class StaffRole(
    val english: String,
    val russian: String?
) {
    companion object {
        fun StaffRole.preferred(titleType: PreferredTitleType): String {
            return when(titleType) {
                PreferredTitleType.RUSSIAN -> russian ?: english
                else -> english
            }
        }
    }
}
