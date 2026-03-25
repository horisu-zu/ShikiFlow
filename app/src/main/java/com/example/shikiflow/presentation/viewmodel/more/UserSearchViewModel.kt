package com.example.shikiflow.presentation.viewmodel.more

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.example.shikiflow.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
@HiltViewModel
class UserSearchViewModel @Inject constructor(
    private val userRepository: UserRepository
): ViewModel() {

    private var _query = MutableStateFlow("")

    val users = _query
        .debounce { query ->
            if(query.isNotBlank()) 500L else 0L
        }
        .distinctUntilChanged()
        .flatMapLatest { query ->
            userRepository.getUsers(query)
        }.cachedIn(viewModelScope)

    fun setQuery(query: String) {
        _query.update { query }
    }
}