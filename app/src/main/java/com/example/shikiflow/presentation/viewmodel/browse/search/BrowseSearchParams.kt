package com.example.shikiflow.presentation.viewmodel.browse.search

import com.example.shikiflow.domain.model.search.MediaBrowseOptions
import com.example.shikiflow.domain.model.tracks.MediaType
import com.example.shikiflow.presentation.screen.browse.main.SearchType

data class BrowseSearchParams(
    val searchType: SearchType = SearchType.MEDIA,
    val mediaBrowseOptions: MediaBrowseOptions = MediaBrowseOptions(MediaType.ANIME)
)
