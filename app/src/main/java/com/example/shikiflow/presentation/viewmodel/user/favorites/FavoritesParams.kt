package com.example.shikiflow.presentation.viewmodel.user.favorites

import com.example.shikiflow.domain.model.user.FavoriteCategory

data class FavoritesParams(
    val userId: Int? = null,
    val currentCategory: FavoriteCategory? = null
)