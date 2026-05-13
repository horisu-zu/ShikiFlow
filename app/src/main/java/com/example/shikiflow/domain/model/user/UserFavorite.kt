package com.example.shikiflow.domain.model.user

import com.example.shikiflow.domain.model.media_details.MediaTitle

data class UserFavorite(
    val id: Int,
    val name: MediaTitle,
    val imageUrl: String? = null,
    val favoriteCategory: FavoriteCategory
)
