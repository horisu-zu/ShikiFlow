package com.example.shikiflow.domain.model.user

data class UserFavorite(
    val id: Int,
    val name: String,
    val imageUrl: String? = null,
    val favoriteCategory: FavoriteCategory
)
