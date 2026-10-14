package com.example.shikiflow.presentation.viewmodel.browse.main.shikimori

import com.example.shikiflow.domain.model.settings.BrowseUiMode
import com.example.shikiflow.domain.model.sort.MediaSort
import com.example.shikiflow.domain.model.tracks.MediaType

interface ShikiBrowseEvent {
    fun loadMore()
    fun refresh()
    fun retry()

    fun setMediaType(mediaType: MediaType)
    fun setBrowseUiMode(mode: BrowseUiMode)
    fun setBrowseOngoingOrder(order: MediaSort)
}