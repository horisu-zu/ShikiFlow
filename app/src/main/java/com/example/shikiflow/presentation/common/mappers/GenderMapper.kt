package com.example.shikiflow.presentation.common.mappers

import com.example.shikiflow.R
import com.example.shikiflow.domain.model.staff.Gender

object GenderMapper {
    fun Gender.displayValue(): Int {
        return when(this) {
            Gender.MALE -> R.string.gender_male
            Gender.FEMALE -> R.string.gender_female
            Gender.OTHER -> R.string.gender_other
        }
    }
}