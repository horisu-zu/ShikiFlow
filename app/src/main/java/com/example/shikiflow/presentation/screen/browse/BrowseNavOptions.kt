package com.example.shikiflow.presentation.screen.browse

import com.example.shikiflow.domain.model.anime.BrowseType
import com.example.shikiflow.presentation.screen.MainNavOptions
import com.example.shikiflow.presentation.screen.MainScreenNavOptions

interface BrowseNavOptions: MainNavOptions, MainScreenNavOptions {
    fun navigateToSideScreen(browseType: BrowseType)
}