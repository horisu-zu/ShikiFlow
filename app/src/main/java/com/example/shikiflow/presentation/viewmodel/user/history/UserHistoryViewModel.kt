package com.example.shikiflow.presentation.viewmodel.user.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.example.shikiflow.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class UserHistoryViewModel @Inject constructor(
    private val userRepository: UserRepository
): ViewModel() {
    private val _userId = MutableStateFlow<Int?>(null)

    val userHistory = _userId
        .filterNotNull()
        .distinctUntilChanged()
        .flatMapLatest { userId ->
            userRepository.getUserHistory(userId)
        }.cachedIn(viewModelScope)

    fun setId(userId: String?) {
        userId?.let {
            _userId.update { userId.toInt() }
        }
    }
}