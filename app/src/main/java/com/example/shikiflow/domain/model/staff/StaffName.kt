package com.example.shikiflow.domain.model.staff

import com.example.shikiflow.domain.model.media_details.PreferredTitleType

data class StaffName(
    val english: String,
    val russian: String?,
    val native: String?
) {
    companion object {
        fun StaffName.preferred(titleType: PreferredTitleType): String {
            return when(titleType) {
                PreferredTitleType.ROMAJI,
                PreferredTitleType.ENGLISH -> english
                PreferredTitleType.RUSSIAN -> russian ?: english
                PreferredTitleType.NATIVE -> native ?: english
            }
        }
    }
}
