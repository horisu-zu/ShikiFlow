package com.example.shikiflow.domain.model.user

import com.example.shikiflow.domain.model.tracks.MediaType
import com.example.shikiflow.domain.model.user.social.SocialCategory

data class UserStatsCategories(
    val scoreMediaTypes: List<MediaType> = emptyList(),
    val favoriteCategories: List<FavoriteCategory> = emptyList(),
    val socialCategories: List<SocialCategory> = emptyList()
)
