package com.example.shikiflow.presentation.viewmodel.user

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.shikiflow.data.local.source.HistoryPagingSource
import com.example.shikiflow.domain.model.user.UserHistoryResponse
import com.example.shikiflow.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class UserHistoryViewModel @Inject constructor(
    private val userRepository: UserRepository
): ViewModel() {

    private val _pagingDataFlowMap = mutableMapOf<Long, Flow<PagingData<UserHistoryResponse>>>()

    fun loadPaginatedHistory(userId: Long): Flow<PagingData<UserHistoryResponse>> {
        return _pagingDataFlowMap.getOrPut(userId) {
            Pager(
                config = PagingConfig(
                    pageSize = 20,
                    enablePlaceholders = true,
                    prefetchDistance = 5,
                    initialLoadSize = 20
                ),
                pagingSourceFactory = { HistoryPagingSource(userRepository, userId) }
            ).flow.cachedIn(viewModelScope)
        }
    }
}