package com.example.shikiflow.presentation.viewmodel.user.social

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.example.shikiflow.domain.model.user.social.SocialCategory
import com.example.shikiflow.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class UserSocialViewModel @Inject constructor(
    private val userRepository: UserRepository
): ViewModel() {

    private val _params = MutableStateFlow(SocialParams())
    val params = _params.asStateFlow()

    val userSocialItems = SocialCategory.entries.associateWith { socialType ->
        _params
            .filter { params ->
                params.userId != null && params.currentCategory == socialType
            }
            .distinctUntilChangedBy { params ->
                params.userId
            }
            .flatMapLatest { params ->
                userRepository.getUserSocial(params.userId!!, socialType)
            }.cachedIn(viewModelScope)
    }

    fun setUserId(userId: Int) {
        _params.update { state ->
            state.copy(userId = userId)
        }
    }

    fun setCategory(socialCategory: SocialCategory) {
        _params.update { state ->
            state.copy(currentCategory = socialCategory)
        }
    }
}