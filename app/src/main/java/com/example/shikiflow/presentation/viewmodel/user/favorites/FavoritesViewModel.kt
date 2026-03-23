package com.example.shikiflow.presentation.viewmodel.user.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.example.shikiflow.domain.model.user.FavoriteCategory
import com.example.shikiflow.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val userRepository: UserRepository
): ViewModel() {

    private val _favoritesParams = MutableStateFlow(FavoritesParams())

    val userFavorites = FavoriteCategory.entries.associateWith { favoriteCategory ->
        _favoritesParams
            .filter { state ->
                state.userId != null && state.currentCategory == favoriteCategory
            }
            .distinctUntilChanged { old, new ->
                old.userId == new.userId
            }
            .flatMapLatest { state ->
                userRepository.getUserFavorites(state.userId!!, favoriteCategory)
            }.cachedIn(viewModelScope)
    }

    fun setUserId(userId: Int) {
        _favoritesParams.update { state ->
            state.copy(userId = userId)
        }
    }

    fun setCategory(favoriteCategory: FavoriteCategory) {
        _favoritesParams.update { state ->
            state.copy(currentCategory = favoriteCategory)
        }
    }
}