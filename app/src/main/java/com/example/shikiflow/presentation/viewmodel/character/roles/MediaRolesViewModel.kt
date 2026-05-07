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
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
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
    val params = _rolesParams.asStateFlow()

    val authType = settingsRepository.authTypeFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = null
        )

    fun setInitialData(
        id: Int,
        roleTypes: List<RoleType>,
        mediaRolesType: MediaRolesType
    ) {
        _rolesParams.update { params ->
            params.copy(
                id = id,
                mediaRolesType = mediaRolesType,
                typeSortMap = params.typeSortMap ?: roleTypes.associateWith { roleType ->
                    when (roleType) {
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
            )
        }
    }

    fun setSort(
        roleType: RoleType,
        roleSort: RoleSort
    ) {
        _rolesParams.update { params ->
            params.copy(
                typeSortMap = params.typeSortMap?.toMutableMap().apply {
                    this?.set(roleType, roleSort)
                }
            )
        }
    }

    val mediaRoles: Map<RoleType, Flow<PagingData<MediaRole>>> = RoleType.entries
        .associateWith { roleType ->
            _rolesParams
                .filter { params ->
                    params.id != null && params.mediaRolesType != null &&
                            params.typeSortMap?.get(roleType) != null
                }
                .distinctUntilChangedBy { params ->
                    params.typeSortMap?.get(roleType)
                }
                .flatMapLatest { params ->
                    when(val currentSort = params.typeSortMap?.get(roleType)!!) {
                        is RoleSort.VA -> staffRepository.getVoiceActorRoles(
                            staffId = params.id!!,
                            sort = currentSort.sort
                        )
                        is RoleSort.Media -> when(params.mediaRolesType!!) {
                            MediaRolesType.CHARACTER -> characterRepository.getCharacterMediaRoles(
                                characterId = params.id!!,
                                mediaType = roleType.toMediaType(),
                                sort = currentSort.sort
                            )
                            MediaRolesType.STAFF -> staffRepository.getStaffMediaRoles(
                                staffId = params.id!!,
                                mediaType = roleType.toMediaType(),
                                sort = currentSort.sort
                            )
                        }
                    }
                }.cachedIn(viewModelScope)
        }
}