package com.example.shikiflow.presentation.viewmodel.browse.search

import com.example.shikiflow.domain.model.search.BrowseOptions

interface BrowseSearchEvent {
    fun updateSearchOptions(browseOptions: BrowseOptions)

    fun onQueryChange(query: String)

    fun onSearchStateChange(isActive: Boolean)
}