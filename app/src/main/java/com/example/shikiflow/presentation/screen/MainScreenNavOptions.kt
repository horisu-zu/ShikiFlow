package com.example.shikiflow.presentation.screen

import com.example.shikiflow.data.tracks.MediaType

interface MainScreenNavOptions : MainNavOptions {
    fun navigateToDetails(mediaId: String, mediaType: MediaType)
}