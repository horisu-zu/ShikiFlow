package com.example.shikiflow.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.graphql.AnimeTracksQuery
import com.example.graphql.type.SortOrderEnum
import com.example.graphql.type.UserRateOrderFieldEnum
import com.example.graphql.type.UserRateOrderInputType
import com.example.graphql.type.UserRateStatusEnum
import com.example.shikiflow.domain.repository.AnimeTracksRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Instant
import kotlinx.datetime.toInstant
import javax.inject.Inject

@HiltViewModel
class AnimeTracksViewModel @Inject constructor(
    private val animeTracksRepository: AnimeTracksRepository
): ViewModel() {
    private val _userRates = MutableStateFlow<Map<UserRateStatusEnum, List<AnimeTracksQuery.UserRate>>>(
        UserRateStatusEnum.entries.associateWith { emptyList() }
    )
    val userRates = _userRates.asStateFlow()

    private val _isLoading = MutableStateFlow<Map<UserRateStatusEnum, Boolean>>(
        UserRateStatusEnum.entries.associateWith { false }
    )
    val isLoading = _isLoading.asStateFlow()

    private val _hasMorePages = MutableStateFlow<Map<UserRateStatusEnum, Boolean>>(
        UserRateStatusEnum.entries.associateWith { true }
    )
    val hasMorePages = _hasMorePages.asStateFlow()

    private val _currentPages = MutableStateFlow<Map<UserRateStatusEnum, Int>>(
        UserRateStatusEnum.entries.associateWith { 1 }
    )

    private val _loadedStatuses = MutableStateFlow<Set<UserRateStatusEnum>>(emptySet())

    fun loadAnimeTracks(
        status: UserRateStatusEnum,
        isRefresh: Boolean = false
    ) {
        if (_isLoading.value[status] == true || _hasMorePages.value[status] == false) return

        viewModelScope.launch {
            _isLoading.update {
                it.toMutableMap().apply {
                    this[status] = true
                }
            }

            if (isRefresh) {
                _currentPages.update {
                    it.toMutableMap().apply {
                        this[status] = 1
                    }
                }
                _userRates.update {
                    it.toMutableMap().apply {
                        this[status] = emptyList()
                    }
                }
                _hasMorePages.update {
                    it.toMutableMap().apply {
                        this[status] = true
                    }
                }
            }

            val result = animeTracksRepository.getAnimeTracks(
                page = _currentPages.value[status] ?: 1,
                status = status,
                order = UserRateOrderInputType(
                    field = UserRateOrderFieldEnum.updated_at,
                    order = SortOrderEnum.desc
                )
            )

            result.onSuccess { response ->
                _userRates.update { currentRates ->
                    currentRates.toMutableMap().apply {
                        val combinedRates = (this[status] ?: emptyList()) + response.userRates

                        val sortedRates = combinedRates.distinctBy { it.animeUserRateWithModel.id }
                            .sortedByDescending {
                                (it.animeUserRateWithModel.updatedAt as? String)?.toInstant() ?: Instant.DISTANT_PAST
                            }

                        this[status] = sortedRates
                    }
                }

                _currentPages.update {
                    it.toMutableMap().apply {
                        if (response.userRates.isNotEmpty()) {
                            this[status] = (this[status] ?: 1) + 1
                        }
                    }
                }

                _hasMorePages.update {
                    it.toMutableMap().apply {
                        this[status] = response.hasNextPage
                    }
                }

                _loadedStatuses.update { it + status }
            }.onFailure {
                _hasMorePages.update {
                    it.toMutableMap().apply {
                        this[status] = false
                    }
                }
            }

            _isLoading.update {
                it.toMutableMap().apply {
                    this[status] = false
                }
            }
        }
    }
}
