package com.example.shikiflow.presentation.viewmodel.manga.read

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shikiflow.domain.model.common.SortDirection
import com.example.shikiflow.domain.usecase.AggregateMangaUseCase
import com.example.shikiflow.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import javax.inject.Inject

data class MangaChaptersUiState(
    val chaptersMap: Map<String, List<String>> = emptyMap(),
    val sortDirection: SortDirection = SortDirection.ASCENDING,

    val errorMessage: String? = null,
    val isLoading: Boolean = true
)

@HiltViewModel
class MangaChaptersViewModel @Inject constructor(
    private val aggregateMangaUseCase: AggregateMangaUseCase
): ViewModel() {

    private val _chaptersUiState = MutableStateFlow(MangaChaptersUiState())
    val chaptersUiState = _chaptersUiState.asStateFlow()

    fun getMangaChapters(mangaDexId: String) {
        if(_chaptersUiState.value.chaptersMap.isNotEmpty()) return

        aggregateMangaUseCase(mangaDexId).onEach { result ->
            when (result) {
                is Resource.Loading -> {
                    Log.d("MangaChaptersViewModel", "Aggregation in progress...")
                    _chaptersUiState.update { state ->
                        state.copy(isLoading = true)
                    }
                }
                is Resource.Success -> {
                    Log.d("MangaChaptersViewModel", "Aggregation successful: ${result.data}")
                    _chaptersUiState.update { state ->
                        state.copy(
                            isLoading = false,
                            chaptersMap = result.data ?: emptyMap()
                        )
                    }
                }
                is Resource.Error -> {
                    Log.e("MangaChaptersViewModel", "Aggregation failed: ${result.message}")
                    _chaptersUiState.update { state ->
                        state.copy(
                            isLoading = false,
                            errorMessage = result.message
                        )
                    }
                }
            }
        }.launchIn(viewModelScope)
    }

    fun changeDirection(sortDirection: SortDirection) {
        _chaptersUiState.update { state ->
            state.copy(sortDirection = sortDirection)
        }
    }
}