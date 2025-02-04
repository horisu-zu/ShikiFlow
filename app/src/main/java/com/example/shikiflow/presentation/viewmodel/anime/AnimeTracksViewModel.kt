package com.example.shikiflow.presentation.viewmodel.anime

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.graphql.AnimeTracksQuery
import com.example.graphql.type.SortOrderEnum
import com.example.graphql.type.UserRateOrderFieldEnum
import com.example.graphql.type.UserRateOrderInputType
import com.example.graphql.type.UserRateStatusEnum
import com.example.shikiflow.data.mapper.UserRateMapper
import com.example.shikiflow.data.mapper.UserRateStatusConstants
import com.example.shikiflow.domain.repository.AnimeTracksRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Instant
import kotlinx.datetime.toInstant
import javax.inject.Inject

@HiltViewModel
class AnimeTracksViewModel @Inject constructor(
    private val animeTracksRepository: AnimeTracksRepository
): ViewModel() {
    data class TrackState(
        val items: List<AnimeTracksQuery.UserRate> = emptyList(),
        val isLoading: Boolean = false,
        val hasMorePages: Boolean = true,
        val currentPage: Int = 1,
        val isLoaded: Boolean = false
    )

    private val _trackStates = MutableStateFlow<Map<UserRateStatusEnum, TrackState>>(
        UserRateStatusEnum.entries.associateWith { TrackState() }
    )
    private val trackStates = _trackStates.asStateFlow()

    val userRates = trackStates.map { states ->
        states.mapValues { it.value.items }
    }.stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        UserRateStatusEnum.entries.associateWith { emptyList() }
    )

    val isLoading = trackStates.map { states ->
        states.mapValues { it.value.isLoading }
    }.stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        UserRateStatusEnum.entries.associateWith { false }
    )

    val hasMorePages = trackStates.map { states ->
        states.mapValues { it.value.hasMorePages }
    }.stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        UserRateStatusEnum.entries.associateWith { true }
    )

    fun refreshAfterStatusUpdate(
        currentStatus: UserRateStatusEnum,
        newStatus: Int
    ) {
        Log.d("Track Refresh", "Received newStatus: $newStatus (${newStatus::class.java})")
        val mappedNewStatus = UserRateMapper.mapStringToStatus(
            UserRateStatusConstants.convertToApiStatus(newStatus)
                .replace("_", " ")
                .split(" ")
                .joinToString(" ") { it.replaceFirstChar { char -> char.uppercase() } }
        ) ?: return
        Log.d("Track Refresh", "Mapped New Status: $mappedNewStatus")

        loadAnimeTracks(currentStatus, isRefresh = true)

        if (currentStatus != mappedNewStatus) {
            Log.d("Track Refresh", "currentStatus: $currentStatus, newStatus: $mappedNewStatus")
            loadAnimeTracks(mappedNewStatus, isRefresh = true)
        }
    }

    fun loadAnimeTracks(
        status: UserRateStatusEnum,
        isRefresh: Boolean = false
    ) {
        val currentState = _trackStates.value[status] ?: return

        if (currentState.isLoading ||
            (currentState.isLoaded && !isRefresh && !currentState.hasMorePages)) return

        viewModelScope.launch {
            _trackStates.update { states ->
                states.toMutableMap().apply {
                    this[status] = currentState.copy(isLoading = true)
                }
            }

            val result = animeTracksRepository.getAnimeTracks(
                page = if (isRefresh) 1 else currentState.currentPage,
                status = status,
                limit = 20,
                order = UserRateOrderInputType(
                    field = UserRateOrderFieldEnum.updated_at,
                    order = SortOrderEnum.desc
                )
            )

            result.onSuccess { response ->
                _trackStates.update { states ->
                    states.toMutableMap().apply {
                        val combinedItems = if (isRefresh) {
                            response.userRates
                        } else {
                            currentState.items + response.userRates
                        }

                        val sortedItems = combinedItems
                            .distinctBy { it.animeUserRateWithModel.id }
                            .sortedByDescending {
                                (it.animeUserRateWithModel.updatedAt as? String)?.toInstant()
                                    ?: Instant.DISTANT_PAST
                            }

                        this[status] = currentState.copy(
                            items = sortedItems,
                            currentPage = if (isRefresh) 2 else currentState.currentPage + 1,
                            hasMorePages = response.hasNextPage,
                            isLoading = false,
                            isLoaded = true
                        )
                    }
                }
            }.onFailure {
                _trackStates.update { states ->
                    states.toMutableMap().apply {
                        this[status] = currentState.copy(
                            hasMorePages = false,
                            isLoading = false,
                            isLoaded = true
                        )
                    }
                }
            }
        }
    }
}