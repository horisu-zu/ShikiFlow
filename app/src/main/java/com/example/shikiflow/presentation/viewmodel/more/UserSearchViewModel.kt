package com.example.shikiflow.presentation.viewmodel.more

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.shikiflow.data.local.source.UserPagingSource
import com.example.shikiflow.domain.model.user.User
import com.example.shikiflow.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import javax.inject.Inject

@HiltViewModel
class UserSearchViewModel @Inject constructor(
    private val userRepository: UserRepository
): ViewModel() {

    private var _query: String? = null
    private var _cache = mutableStateOf<Flow<PagingData<User>>>(emptyFlow())

    fun paginatedUsers(query: String): Flow<PagingData<User>> {
        val pagerFlow = {
            Pager(
                config = PagingConfig(
                    pageSize = 30,
                    enablePlaceholders = true,
                    prefetchDistance = 10,
                    initialLoadSize = 30
                ),
                pagingSourceFactory = {
                    UserPagingSource(
                        userRepository = userRepository,
                        query = query
                    )
                }
            ).flow.cachedIn(viewModelScope)
        }

        return if(_query != query) {
            _query = query
            pagerFlow().also { flow ->
                _cache.value = flow
            }
        } else {
            _cache.value
        }
    }
}