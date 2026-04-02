package com.example.shikiflow.presentation.screen.browse.side

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.runtime.Composable
import com.example.shikiflow.domain.model.browse.BrowseType
import com.example.shikiflow.domain.model.tracks.MediaType
import com.example.shikiflow.presentation.screen.browse.BrowseNavOptions
import com.example.shikiflow.presentation.screen.browse.ongoings.OngoingSideScreen
import com.example.shikiflow.presentation.screen.main.details.DetailsNavRoute

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun BrowseSideScreen(
    browseType: BrowseType,
    navOptions: BrowseNavOptions
) {
    if(browseType == BrowseType.AnimeBrowseType.ONGOING) {
        OngoingSideScreen(
            browseType = browseType,
            onNavigate = { id ->
                navOptions.navigateToDetails(DetailsNavRoute.AnimeDetails(id))
            },
            onBackNavigate = { navOptions.navigateBack() }
        )
    } else {
        MainSideScreen(
            browseType = browseType,
            onMediaNavigate = { id, mediaType ->
                val detailsNavRoute = when(mediaType) {
                    MediaType.ANIME -> DetailsNavRoute.AnimeDetails(id)
                    MediaType.MANGA -> DetailsNavRoute.MangaDetails(id)
                }

                navOptions.navigateToDetails(detailsNavRoute)
            },
            onBackNavigate = { navOptions.navigateBack() }
        )
    }
}