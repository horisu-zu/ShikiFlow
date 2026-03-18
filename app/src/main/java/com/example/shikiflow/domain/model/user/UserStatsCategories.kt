package com.example.shikiflow.domain.model.user

import com.example.shikiflow.domain.model.tracks.MediaType

data class UserStatsCategories(
    val scoreMediaTypes: List<MediaType> = emptyList(),
    val favoriteCategories: List<FavoriteCategory> = emptyList()
)
