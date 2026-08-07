package com.example.shikiflow.presentation.viewmodel.user.favorites

import com.example.shikiflow.domain.model.auth.AuthType
import com.example.shikiflow.domain.model.user.FavoriteCategory

data class FavoritesParams(
    val userId: Int? = null,
    val authType: AuthType? = null,
    val currentCategory: FavoriteCategory? = null
)