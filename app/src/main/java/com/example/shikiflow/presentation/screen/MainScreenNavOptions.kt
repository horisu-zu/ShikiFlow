package com.example.shikiflow.presentation.screen

import com.example.shikiflow.domain.model.tracks.MediaType

interface MainScreenNavOptions : MainNavOptions {
    fun navigateToDetails(mediaId: Int, mediaType: MediaType)
}