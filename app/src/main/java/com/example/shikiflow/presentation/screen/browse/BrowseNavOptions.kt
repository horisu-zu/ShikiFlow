package com.example.shikiflow.presentation.screen.browse

import com.example.shikiflow.domain.model.anime.BrowseType
import com.example.shikiflow.presentation.screen.MainNavOptions

interface BrowseNavOptions: MainNavOptions {
    fun navigateToSideScreen(browseType: BrowseType)
}