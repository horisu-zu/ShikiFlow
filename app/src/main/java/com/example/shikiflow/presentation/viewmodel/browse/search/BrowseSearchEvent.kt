package com.example.shikiflow.presentation.viewmodel.browse.search

import com.example.shikiflow.domain.model.search.MediaBrowseOptions
import com.example.shikiflow.presentation.screen.browse.main.SearchType

interface BrowseSearchEvent {
    fun setSearchType(searchType: SearchType)

    fun updateSearchOptions(browseOptions: MediaBrowseOptions)

    fun onQueryChange(query: String)

    fun onSearchStateChange(isActive: Boolean)
}