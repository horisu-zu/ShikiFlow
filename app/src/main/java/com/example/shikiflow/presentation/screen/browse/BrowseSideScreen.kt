package com.example.shikiflow.presentation.screen.browse

import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.shikiflow.data.anime.BrowseType
import com.example.shikiflow.presentation.viewmodel.anime.BrowseViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BrowseSideScreen(
    browseType: BrowseType,
    rootNavController: NavController,
    browseNavController: NavController,
    browseViewModel: BrowseViewModel = hiltViewModel()
) {
    val sideScreenData = browseViewModel.paginatedBrowse(browseType).collectAsLazyPagingItems()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = browseType.toString().lowercase().replaceFirstChar { it.uppercase() }
                            .replace("_", " "),
                        style = MaterialTheme.typography.headlineSmall
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = { browseNavController.popBackStack() }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { innerPadding ->
        if (browseType == BrowseType.AnimeBrowseType.ONGOING) {
            OngoingSideScreen(
                ongoingData = sideScreenData,
                modifier = Modifier.padding(top = innerPadding.calculateTopPadding(),
                    start = innerPadding.calculateStartPadding(LayoutDirection.Ltr),
                    end = innerPadding.calculateEndPadding(LayoutDirection.Ltr)).padding(horizontal = 12.dp),
                rootNavController = rootNavController
            )
        } else {
            MainSideScreen(
                browseData = sideScreenData,
                modifier = Modifier.padding(top = innerPadding.calculateTopPadding(),
                    start = innerPadding.calculateStartPadding(LayoutDirection.Ltr),
                    end = innerPadding.calculateEndPadding(LayoutDirection.Ltr)).padding(horizontal = 12.dp),
                rootNavController = rootNavController
            )
        }
    }
}