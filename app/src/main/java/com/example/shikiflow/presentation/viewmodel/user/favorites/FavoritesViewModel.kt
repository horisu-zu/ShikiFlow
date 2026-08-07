package com.example.shikiflow.presentation.viewmodel.user.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.example.shikiflow.domain.model.user.FavoriteCategory
import com.example.shikiflow.domain.repository.SettingsRepository
import com.example.shikiflow.domain.repository.UserRepository
import com.example.shikiflow.utils.result.DataResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val settingsRepository: SettingsRepository
): ViewModel() {

    private val _favoritesParams = MutableStateFlow(FavoritesParams())
    val params = _favoritesParams.asStateFlow()

    private val _orderEvent = MutableSharedFlow<Unit>()
    val orderEvent = _orderEvent.asSharedFlow()

    val userFavorites = FavoriteCategory.entries.associateWith { favoriteCategory ->
        _favoritesParams
            .filter { params ->
                params.userId != null && params.currentCategory == favoriteCategory
            }
            .distinctUntilChangedBy { params ->
                params.userId
            }
            .flatMapLatest { state ->
                userRepository.getUserFavorites(state.userId!!, favoriteCategory)
            }.cachedIn(viewModelScope)
    }

    init {
        settingsRepository.authTypeFlow
            .filterNotNull()
            .distinctUntilChanged()
            .onEach { authType ->
                _favoritesParams.update { params ->
                    params.copy(authType = authType)
                }
            }.launchIn(viewModelScope)
    }

    fun changeOrder(
        reorderedIds: List<Int>,
        favoriteCategory: FavoriteCategory
    ) {
        viewModelScope.launch {
            userRepository.changeFavoritesOrder(reorderedIds, favoriteCategory).let { result ->
                if (result is DataResult.Success) {
                    _orderEvent.emit(Unit)
                }
            }
        }
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