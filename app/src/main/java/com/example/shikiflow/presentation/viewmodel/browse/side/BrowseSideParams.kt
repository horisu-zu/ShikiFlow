package com.example.shikiflow.presentation.viewmodel.browse.side

import com.example.shikiflow.domain.model.anime.BrowseType
import com.example.shikiflow.domain.model.auth.AuthType

data class BrowseSideParams(
    val browseType: BrowseType? = null,
    val authType: AuthType? = null
)
