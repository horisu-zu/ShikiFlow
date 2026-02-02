package com.example.shikiflow.data.datasource

import com.example.shikiflow.domain.model.staff.StaffDetails

interface StaffDataSource {
    suspend fun getStaffDetails(staffId: Int): Result<StaffDetails>


}