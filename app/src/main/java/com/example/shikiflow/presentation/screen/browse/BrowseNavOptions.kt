package com.example.shikiflow.presentation.screen.browse

import com.example.shikiflow.data.anime.BrowseType
import com.example.shikiflow.presentation.screen.MainNavOptions
import com.example.shikiflow.presentation.screen.MainScreenNavOptions
import com.example.shikiflow.presentation.screen.MediaNavOptions

interface BrowseNavOptions: MainNavOptions, MainScreenNavOptions {
    fun navigateToSideScreen(browseType: BrowseType)
}