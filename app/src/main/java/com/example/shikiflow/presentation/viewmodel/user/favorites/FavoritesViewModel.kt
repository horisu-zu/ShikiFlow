package com.example.shikiflow.presentation.viewmodel.user.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.shikiflow.domain.model.user.FavoriteCategory
import com.example.shikiflow.domain.model.user.UserFavorite
import com.example.shikiflow.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val userRepository: UserRepository
): ViewModel() {

    private val _pagingFavoritesMap = mutableMapOf<FavoriteCategory, Flow<PagingData<UserFavorite>>>()

    fun loadUserFavorites(
        userId: String,
        favoriteCategory: FavoriteCategory
    ): Flow<PagingData<UserFavorite>> {
        return _pagingFavoritesMap.getOrPut(favoriteCategory) {
            userRepository.getUserFavorites(userId.toInt(), favoriteCategory)
                .cachedIn(viewModelScope)
        }
    }
}