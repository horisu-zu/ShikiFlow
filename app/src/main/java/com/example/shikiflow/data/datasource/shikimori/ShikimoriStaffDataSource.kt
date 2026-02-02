package com.example.shikiflow.data.datasource.shikimori

import com.example.shikiflow.data.datasource.StaffDataSource
import com.example.shikiflow.data.mapper.shikimori.ShikimoriStaffMapper.toDomain
import com.example.shikiflow.data.remote.PersonApi
import com.example.shikiflow.domain.model.staff.StaffDetails
import javax.inject.Inject

class ShikimoriStaffDataSource @Inject constructor(
    private val staffApi: PersonApi
): StaffDataSource {
    override suspend fun getStaffDetails(staffId: Int): Result<StaffDetails> {
        return try {
            val response = staffApi.getPersonDetails(staffId.toString())

            Result.success(response.toDomain())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}