package com.example.shikiflow.presentation.viewmodel.user

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.shikiflow.domain.model.user.UserHistory
import com.example.shikiflow.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class UserHistoryViewModel @Inject constructor(
    private val userRepository: UserRepository
): ViewModel() {

    private val _pagingDataFlowMap = mutableMapOf<Int, Flow<PagingData<UserHistory>>>()

    fun loadPaginatedHistory(userId: Int): Flow<PagingData<UserHistory>> {
        return _pagingDataFlowMap.getOrPut(userId) {
            userRepository.getUserHistory(
                userId = userId
            ).cachedIn(viewModelScope)
        }
    }
}