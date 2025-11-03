package com.example.shikiflow.domain.model.user

import com.example.shikiflow.domain.model.favorite.ShikiFavorite
import com.example.shikiflow.domain.model.tracks.UserRate

data class UserRateExpanded(
    val userRates: List<UserRate>,
    val userFavorites: List<ShikiFavorite>
)
