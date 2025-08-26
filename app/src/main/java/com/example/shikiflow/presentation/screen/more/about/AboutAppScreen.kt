package com.example.shikiflow.presentation.screen.more.about

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.example.shikiflow.BuildConfig
import com.example.shikiflow.R
import com.example.shikiflow.domain.model.common.SectionItem
import com.example.shikiflow.presentation.screen.more.Section
import com.example.shikiflow.presentation.viewmodel.AboutViewModel
import com.example.shikiflow.utils.IconResource
import com.example.shikiflow.utils.WebIntent

@Composable
fun AboutAppScreen(
    aboutViewModel: AboutViewModel
) {
    val context = LocalContext.current
    val latestRelease = aboutViewModel.latestRelease
    val currentVersion = aboutViewModel.currentVersion
    val showBottomSheet = remember { mutableStateOf(false) }

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
                    showBottomSheet = { showBottomSheet.value = true },
                    context = context,
                    modifier = Modifier
                )
            }
            Section(
                items = listOf(
                    SectionItem.General(
                        icon = IconResource.Drawable(R.drawable.ic_github),
                        title = "Github",
                        subtitle = "Link to Repository",
                        onClick = { WebIntent.openUrlCustomTab(context, BuildConfig.REPO_URL) }
                    ),
                    SectionItem.General(
                        icon = IconResource.Drawable(R.drawable.shiki_logo),
                        title = "Shikimori",
                        subtitle = "Encyclopedia of Anime and Manga",
                        onClick = { WebIntent.openUrlCustomTab(context, BuildConfig.BASE_URL) }
                    )
                )
            )
        }
    }

    latestRelease?.let {
        ReleaseNotesBottomSheet(
            currentVersion = currentVersion?.tagName ?: "Unknown",
            release = latestRelease,
            onDismiss = { showBottomSheet.value = false },
            onDownloadReleaseClick = {
                WebIntent.openUrlCustomTab(
                    context = context,
                    url = latestRelease.assets.firstOrNull()?.downloadUrl ?: "Url"
                )
            },
            showBottomSheet = showBottomSheet.value,
            modifier = Modifier
        )
    }
}