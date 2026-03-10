package com.example.shikiflow.presentation.viewmodel.anime

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.shikiflow.domain.model.anime.Browse
import com.example.shikiflow.domain.model.sort.OrderOption
import com.example.shikiflow.domain.repository.MediaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.transformLatest
import kotlinx.coroutines.flow.update
import javax.inject.Inject

data class StudioUiState(
    val titleQuery: String = "",
    val onUserList: Boolean? = null
)

@OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
@HiltViewModel
class StudioViewModel @Inject constructor(
    private val mediaRepository: MediaRepository
): ViewModel() {

    private val _studioUiState = MutableStateFlow(StudioUiState())
    val studioUiState = _studioUiState.asStateFlow()

    private var _cacheQuery: String = ""
    private var _studioCache = mutableMapOf<Int, Flow<PagingData<Browse>>>()

    fun getStudioAnime(
        studioId: Int,
        orderOption: OrderOption
    ): Flow<PagingData<Browse>> {
        return _studioCache.getOrPut(studioId) {
            _studioUiState
                .transformLatest { state ->
                    if(state.titleQuery != _cacheQuery) {
                        delay(500L)
                        _cacheQuery = state.titleQuery
                    }
                    emit(state)
                }
                .flatMapLatest { (title, onList) ->
                    mediaRepository.getStudioMedia(
                        studioId,
                        search = title,
                        order = orderOption,
                        onList = onList
                    )
                }.cachedIn(viewModelScope)
        }
    }

    fun onUserListSearchChange(value: Boolean) {
        _studioUiState.update { state ->
            state.copy(
                onUserList = value
            )
        }
    }

    fun updateTitleQuery(newQuery: String) {
        _studioUiState.update { state ->
            state.copy(
                titleQuery = newQuery
            )
        }
    }
}