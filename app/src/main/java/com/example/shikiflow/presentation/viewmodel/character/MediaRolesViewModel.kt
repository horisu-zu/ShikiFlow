package com.example.shikiflow.presentation.viewmodel.character

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.shikiflow.domain.model.auth.AuthType
import com.example.shikiflow.domain.model.common.MediaRole
import com.example.shikiflow.domain.model.sort.CharacterType
import com.example.shikiflow.domain.model.sort.MediaSort
import com.example.shikiflow.domain.model.sort.Sort
import com.example.shikiflow.domain.model.sort.SortDirection
import com.example.shikiflow.presentation.screen.main.details.MediaRolesType
import com.example.shikiflow.presentation.screen.main.details.RoleType
import com.example.shikiflow.presentation.screen.main.details.RoleType.Companion.toMediaType
import com.example.shikiflow.domain.repository.CharacterRepository
import com.example.shikiflow.domain.repository.StaffRepository
import com.example.shikiflow.presentation.screen.main.details.RoleSort
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

    private val _sortMap = MutableStateFlow<Map<RoleType, RoleSort>>(emptyMap())
    val sortMap = _sortMap.asStateFlow()

    private val _rolesCache = mutableMapOf<MediaRolesParams, Flow<PagingData<MediaRole>>>()

    fun initializeSortMap(
        roleTypes: List<RoleType>,
        authType: AuthType
    ) {
        if(_sortMap.value.isNotEmpty()) return

        _sortMap.update {
            roleTypes.associateWith { roleType ->
                when(roleType) {
                    RoleType.VA -> RoleSort.VA(
                        Sort(type = CharacterType.FAVORITES, direction = SortDirection.DESCENDING)
                    )
                    else -> RoleSort.Media(
                        sort = when (authType) {
                            AuthType.ANILIST -> Sort(
                                type = MediaSort.Anilist.POPULARITY,
                                direction = SortDirection.DESCENDING
                            )
                            AuthType.SHIKIMORI -> Sort(
                                type = MediaSort.Shikimori.POPULARITY,
                                direction = SortDirection.DESCENDING
                            )
                        }
                    )
                }
            }
        }
    }

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
        roleSort: RoleSort
    ): Flow<PagingData<MediaRole>> {
        return when(roleSort) {
            is RoleSort.VA -> staffRepository.getVoiceActorRoles(
                staffId = id,
                sort = roleSort.sort
            )
            is RoleSort.Media -> when(mediaRolesType) {
                MediaRolesType.CHARACTER -> characterRepository.getCharacterMediaRoles(
                    characterId = id,
                    mediaType = roleType.toMediaType(),
                    sort = roleSort.sort
                )
                MediaRolesType.STAFF -> staffRepository.getStaffMediaRoles(
                    staffId = id,
                    mediaType = roleType.toMediaType(),
                    sort = roleSort.sort
                )
            }
        }
    }

    fun setSort(
        roleType: RoleType,
        roleSort: RoleSort
    ) {
        _sortMap.update { currentMap ->
            currentMap.toMutableMap().apply {
                this[roleType] = roleSort
            }
        }
    }
}