package com.example.shikiflow.presentation.screen.browse

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.runtime.Composable
import com.example.shikiflow.domain.model.anime.BrowseType
import com.example.shikiflow.domain.model.tracks.MediaType
import com.example.shikiflow.presentation.screen.browse.ongoings.OngoingSideScreen
import com.example.shikiflow.presentation.screen.main.details.DetailsNavRoute

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun BrowseSideScreen(
    browseType: BrowseType,
    navOptions: BrowseNavOptions,
    onBackNavigate: () -> Unit
) {
    if(browseType == BrowseType.AnimeBrowseType.ONGOING) {
        OngoingSideScreen(
            browseType = browseType,
            onNavigate = { id ->
                navOptions.navigateToDetails(DetailsNavRoute.AnimeDetails(id))
            },
            onBackNavigate = onBackNavigate
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
            onBackNavigate = onBackNavigate
        )
    }
}