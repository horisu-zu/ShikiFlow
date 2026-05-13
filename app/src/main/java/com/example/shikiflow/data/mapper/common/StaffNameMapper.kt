package com.example.shikiflow.data.mapper.common

import com.example.shikiflow.domain.model.staff.StaffName

object StaffNameMapper {
    fun String.toStaffName(russian: String? = null, native: String?): StaffName {
        return StaffName(
            english = this,
            russian = russian,
            native = native
        )
    }
}