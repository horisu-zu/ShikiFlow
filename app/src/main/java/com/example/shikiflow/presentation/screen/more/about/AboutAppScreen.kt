package com.example.shikiflow.presentation.screen.more.about

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.example.shikiflow.R
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.shikiflow.data.common.SectionItem
import com.example.shikiflow.presentation.screen.more.Section
import com.example.shikiflow.presentation.viewmodel.AboutViewModel
import com.example.shikiflow.utils.IconResource

@Composable
fun AboutAppScreen(
    aboutViewModel: AboutViewModel = hiltViewModel()
) {
    val latestRelease = aboutViewModel.latestRelease
    val currentVersion = aboutViewModel.currentVersion

    Scaffold { innerPadding ->
        Column(
            modifier = Modifier.fillMaxWidth().padding(
                top = innerPadding.calculateTopPadding() + 12.dp,
                start = innerPadding.calculateStartPadding(LayoutDirection.Ltr),
                end = innerPadding.calculateEndPadding(LayoutDirection.Ltr),
            ), verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.Top)
        ) {
            currentVersion?.let { version ->
                CurrentVersionItem(
                    currentRelease = version,
                    modifier = Modifier
                )
            }
            latestRelease?.let { newRelease ->
                LatestReleaseItem(
                    latestRelease = newRelease,
                    modifier = Modifier
                )
            }
            Section(
                items = listOf(
                    SectionItem.General(
                        icon = IconResource.Drawable(R.drawable.ic_github),
                        title = "Github",
                        subtitle = "Link to Repository",
                        onClick = { /*Open Github Link*/ }
                    ),
                    SectionItem.General(
                        icon = IconResource.Drawable(R.drawable.shiki_logo),
                        title = "Shikimori",
                        subtitle = "Encyclopedia of Anime and Manga",
                        onClick = { /*Open Shikimori Link*/ }
                    )
                )
            )
        }
    }
}