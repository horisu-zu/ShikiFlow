package com.example.shikiflow.presentation.viewmodel.character

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.shikiflow.domain.model.common.MediaRole
import com.example.shikiflow.domain.model.sort.CharacterType
import com.example.shikiflow.domain.model.sort.MediaSort
import com.example.shikiflow.domain.model.sort.Sort
import com.example.shikiflow.domain.model.sort.SortDirection
import com.example.shikiflow.presentation.screen.main.details.MediaRolesType
import com.example.shikiflow.presentation.screen.main.details.RoleType
import com.example.shikiflow.presentation.screen.main.details.RoleType.Companion.toMediaType
import com.example.shikiflow.domain.model.sort.SortType
import com.example.shikiflow.domain.repository.CharacterRepository
import com.example.shikiflow.domain.repository.StaffRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.update
import javax.inject.Inject

private data class MediaRolesParams(
    val mediaRolesType: MediaRolesType,
    val roleType: RoleType
)

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class MediaRolesViewModel @Inject constructor(
    private val characterRepository: CharacterRepository,
    private val staffRepository: StaffRepository
): ViewModel() {

    private val _sortMap = MutableStateFlow<Map<RoleType, Sort<SortType>>>(
        value = RoleType.entries.associateWith { roleType ->
            when(roleType) {
                RoleType.VA -> Sort(CharacterType.FAVORITES, SortDirection.DESCENDING)
                else -> Sort(MediaSort.Anilist.POPULARITY, SortDirection.DESCENDING)
            }
        }
    )
    val sortMap = _sortMap.asStateFlow()

    private val _rolesCache = mutableMapOf<MediaRolesParams, Flow<PagingData<MediaRole>>>()

    fun getMediaRoles(
        id: Int,
        roleType: RoleType,
        mediaRolesType: MediaRolesType
    ): Flow<PagingData<MediaRole>> {
        val key = MediaRolesParams(mediaRolesType, roleType)

        return _rolesCache.getOrPut(key) {
            _sortMap
                .mapNotNull { typeSortMap ->
                    typeSortMap[roleType]
                }
                .distinctUntilChanged()
                .flatMapLatest { currentSort ->
                    fetchMediaRoles(id, roleType, mediaRolesType, currentSort)
                }.cachedIn(viewModelScope)
        }
    }

    fun fetchMediaRoles(
        id: Int,
        roleType: RoleType,
        mediaRolesType: MediaRolesType,
        sort: Sort<SortType>
    ): Flow<PagingData<MediaRole>> {
        return when(roleType) {
            RoleType.VA -> {
                staffRepository.getVoiceActorRoles(
                    staffId = id,
                    sort = sort as Sort<CharacterType>
                )
            }
            else -> when(mediaRolesType) {
                MediaRolesType.CHARACTER -> characterRepository.getCharacterMediaRoles(
                    characterId = id,
                    mediaType = roleType.toMediaType(),
                    sort = sort as Sort<MediaSort>
                )
                MediaRolesType.STAFF -> staffRepository.getStaffMediaRoles(
                    staffId = id,
                    mediaType = roleType.toMediaType(),
                    sort = sort as Sort<MediaSort>
                )
            }
        }
    }

    fun setSort(
        roleType: RoleType,
        sort: Sort<SortType>
    ) {
        _sortMap.update { currentMap ->
            currentMap.toMutableMap().apply {
                this[roleType] = sort
            }
        }
    }
}