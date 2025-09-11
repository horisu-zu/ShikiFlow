package com.example.shikiflow.presentation.viewmodel

import androidx.lifecycle.ViewModel
import com.example.shikiflow.domain.model.search.SearchState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(): ViewModel() {

    private val _screenState = MutableStateFlow(SearchState())
    val screenState: StateFlow<SearchState> = _screenState.asStateFlow()

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