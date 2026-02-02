package com.example.shikiflow.domain.repository

import com.example.shikiflow.domain.model.staff.StaffDetails

interface PersonRepository {
    suspend fun getStaffDetails(id: Int): Result<StaffDetails>
}