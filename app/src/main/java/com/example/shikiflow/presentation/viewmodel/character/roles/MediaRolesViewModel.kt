package com.example.shikiflow.presentation.viewmodel.character.roles

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
import com.example.shikiflow.domain.repository.CharacterRepository
import com.example.shikiflow.domain.repository.SettingsRepository
import com.example.shikiflow.domain.repository.StaffRepository
import com.example.shikiflow.presentation.screen.main.details.RoleSort
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class MediaRolesViewModel @Inject constructor(
    private val characterRepository: CharacterRepository,
    private val staffRepository: StaffRepository,
    settingsRepository: SettingsRepository
): ViewModel() {

    private val _rolesParams = MutableStateFlow(MediaRolesParams())

    private val _sortMap = MutableStateFlow<Map<RoleType, RoleSort>>(emptyMap())
    val sortMap = _sortMap.asStateFlow()

    val authType = settingsRepository.authTypeFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = null
        )

    fun setRoleTypes(roleTypes: List<RoleType>) {
        _rolesParams.update { params ->
            params.copy(roleTypes = roleTypes)
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

    init {
        _rolesParams
            .filter { params ->
                params.roleTypes != null
            }
            .distinctUntilChangedBy { params ->
                params.roleTypes
            }
            .onEach { params ->
                _sortMap.update {
                    params.roleTypes!!.associateWith { roleType ->
                        when(roleType) {
                            RoleType.VA -> RoleSort.VA(
                                Sort(type = CharacterType.FAVORITES, direction = SortDirection.DESCENDING)
                            )
                            else -> RoleSort.Media(
                                sort = Sort(
                                    type = MediaSort.Common.POPULARITY,
                                    direction = SortDirection.DESCENDING
                                )
                            )
                        }
                    }
                }
            }.launchIn(viewModelScope)
    }

    fun getMediaRoles(
        id: Int,
        roleType: RoleType,
        mediaRolesType: MediaRolesType
    ): Flow<PagingData<MediaRole>> {
        return _sortMap
            .mapNotNull { typeSortMap ->
                typeSortMap[roleType]
            }
            .distinctUntilChanged()
            .flatMapLatest { currentSort ->
                when(currentSort) {
                    is RoleSort.VA -> staffRepository.getVoiceActorRoles(
                        staffId = id,
                        sort = currentSort.sort
                    )
                    is RoleSort.Media -> when(mediaRolesType) {
                        MediaRolesType.CHARACTER -> characterRepository.getCharacterMediaRoles(
                            characterId = id,
                            mediaType = roleType.toMediaType(),
                            sort = currentSort.sort
                        )
                        MediaRolesType.STAFF -> staffRepository.getStaffMediaRoles(
                            staffId = id,
                            mediaType = roleType.toMediaType(),
                            sort = currentSort.sort
                        )
                    }
                }
            }.cachedIn(viewModelScope)
    }
}