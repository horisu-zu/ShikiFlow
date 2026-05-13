package com.example.shikiflow.data.mapper.common

import com.example.shikiflow.domain.model.staff.StaffRole

object StaffRoleMapper {
    fun String.toStaffRole(russian: String? = null): StaffRole {
        return StaffRole(
            english = this,
            russian = russian
        )
    }
}