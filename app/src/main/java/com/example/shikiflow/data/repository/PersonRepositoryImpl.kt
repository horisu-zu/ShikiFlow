package com.example.shikiflow.data.repository

import com.example.shikiflow.data.datasource.StaffDataSource
import com.example.shikiflow.domain.model.auth.AuthType
import com.example.shikiflow.domain.model.staff.StaffDetails
import com.example.shikiflow.domain.repository.PersonRepository
import com.example.shikiflow.domain.repository.SettingsRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
class PersonRepositoryImpl @Inject constructor(
    private val anilistDataSource: StaffDataSource,
    private val shikimoriDataSource: StaffDataSource,
    private val settingsRepository: SettingsRepository
) : PersonRepository {

    private fun getSource() = when(settingsRepository.authTypeFlow.value) {
        AuthType.SHIKIMORI -> shikimoriDataSource
        AuthType.ANILIST -> anilistDataSource
    }

    override suspend fun getStaffDetails(id: Int): Result<StaffDetails> {
        return getSource().getStaffDetails(id)
    }
}