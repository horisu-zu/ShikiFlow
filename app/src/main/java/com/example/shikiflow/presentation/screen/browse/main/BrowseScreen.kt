package com.example.shikiflow.presentation.screen.browse.main

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.shikiflow.domain.model.auth.AuthType
import com.example.shikiflow.presentation.screen.browse.main.anilist.AnilistBrowseMainPage
import com.example.shikiflow.presentation.screen.browse.BrowseNavOptions
import com.example.shikiflow.presentation.screen.browse.main.shikimori.ShikimoriBrowseMainPage
import com.example.shikiflow.presentation.viewmodel.browse.main.BrowseViewModel

@Composable
fun BrowseScreen(
    browseNavOptions: BrowseNavOptions,
    browseViewModel: BrowseViewModel = hiltViewModel()
) {
    val horizontalPadding = 12.dp
    var isAtTop by remember { mutableStateOf(true) }
    val authType by browseViewModel.authType.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            BrowseSearchBar(
                isAtMainTop = isAtTop,
                navOptions = browseNavOptions,
                horizontalPadding = horizontalPadding,
                modifier = Modifier.fillMaxWidth()
            )
        }
    ) { paddingValues ->
        authType?.let { authType ->
            when(authType) {
                AuthType.SHIKIMORI -> {
                    ShikimoriBrowseMainPage(
                        paddingValues = paddingValues,
                        horizontalPadding = horizontalPadding,
                        isAtTop = isAtTop,
                        browseNavOptions = browseNavOptions,
                        onIsAtTopChange = { isAtTop = it }
                    )
                }
                AuthType.ANILIST -> {
                    AnilistBrowseMainPage(
                        paddingValues = paddingValues,
                        horizontalPadding = horizontalPadding,
                        browseNavOptions = browseNavOptions,
                        onIsAtTopChange = { isAtTop = it }
                    )
                }
            }
        }
    }
}