package com.example.shikiflow.presentation.viewmodel.character

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.shikiflow.domain.model.common.MediaRole
import com.example.shikiflow.domain.model.common.MediaRolesType
import com.example.shikiflow.domain.model.common.RoleType
import com.example.shikiflow.domain.model.common.RoleType.Companion.toMediaType
import com.example.shikiflow.domain.repository.CharacterRepository
import com.example.shikiflow.domain.repository.StaffRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class MediaRolesViewModel @Inject constructor(
    private val characterRepository: CharacterRepository,
    private val staffRepository: StaffRepository
): ViewModel() {

    private data class CacheKey(
        val mediaRolesType: MediaRolesType,
        val roleType: RoleType
    )
    private val _typeCache = mutableMapOf<CacheKey, Flow<PagingData<MediaRole>>>()

    fun getMediaRoles(
        id: Int,
        mediaRolesType: MediaRolesType,
        roleType: RoleType
    ): Flow<PagingData<MediaRole>> {
        val cacheKey = CacheKey(mediaRolesType, roleType)

        return _typeCache.getOrPut(cacheKey) {
            when(mediaRolesType) {
                MediaRolesType.CHARACTER ->
                    characterRepository.getCharacterMediaRoles(id, roleType.toMediaType())
                MediaRolesType.STAFF -> {
                    when(roleType) {
                        RoleType.VA -> staffRepository.getVoiceActorRoles(id)
                        else -> staffRepository.getStaffMediaRoles(id, roleType.toMediaType())
                    }
                }
            }.cachedIn(viewModelScope)
        }
    }
}