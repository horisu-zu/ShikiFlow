package com.example.shikiflow.presentation.viewmodel.more

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shikiflow.domain.model.search.ScreenSearchState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@OptIn(FlowPreview::class)
@HiltViewModel
class MoreScreenViewModel @Inject constructor(): ViewModel() {
    private val _screenState = MutableStateFlow(ScreenSearchState())
    val screenState = _screenState.asStateFlow()

    val searchQuery = _screenState
        .map { it.query }
        .debounce(500L)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = ""
        )

    fun onQueryChange(newQuery: String) {
        _screenState.update { it.copy(query = newQuery) }
    }

    fun onSearchActiveChange(isActive: Boolean) {
        _screenState.update { it.copy(isSearchActive = isActive) }
    }

    fun exitSearchState() {
        _screenState.update { it.copy(
            isSearchActive = false,
            query = ""
        ) }
    }
}