package com.example.shikiflow.domain.model.user

import com.example.shikiflow.domain.model.favorite.FavoriteCategory
import com.example.shikiflow.domain.model.favorite.ShikiFavorite
import com.example.shikiflow.domain.model.tracks.MediaType
import com.example.shikiflow.domain.model.tracks.UserRate

data class UserRateExpanded(
    val userRates: Map<MediaType, List<UserRate>>,
    val userFavorites: Map<FavoriteCategory, List<ShikiFavorite>>
)
