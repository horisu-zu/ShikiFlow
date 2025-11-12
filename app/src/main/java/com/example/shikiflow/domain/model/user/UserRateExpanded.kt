package com.example.shikiflow.domain.model.user

import com.example.shikiflow.domain.model.favorite.FavoriteCategory
import com.example.shikiflow.domain.model.favorite.ShikiFavorite
import com.example.shikiflow.domain.model.tracks.MediaType

data class UserRateExpanded(
    val userRates: Map<MediaType, Map<Int, Int>>,
    val userFavorites: Map<FavoriteCategory, List<ShikiFavorite>>
)
