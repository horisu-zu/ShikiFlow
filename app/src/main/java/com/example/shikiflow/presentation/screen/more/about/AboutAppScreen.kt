package com.example.shikiflow.presentation.screen.more.about

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.shikiflow.BuildConfig
import com.example.shikiflow.R
import com.example.shikiflow.domain.model.common.SectionItem
import com.example.shikiflow.presentation.screen.more.Section
import com.example.shikiflow.presentation.viewmodel.more.AboutViewModel
import com.example.shikiflow.utils.IconResource
import com.example.shikiflow.utils.WebIntent

@Composable
fun AboutAppScreen(
    aboutViewModel: AboutViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val latestRelease by aboutViewModel.latestRelease.collectAsStateWithLifecycle()
    val currentVersion = aboutViewModel.currentRelease
    val showBottomSheet = remember { mutableStateOf(false) }

    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(
                    top = innerPadding.calculateTopPadding() + 12.dp,
                    start = innerPadding.calculateStartPadding(LayoutDirection.Ltr) + 24.dp,
                    end = innerPadding.calculateEndPadding(LayoutDirection.Ltr) + 24.dp,
                    bottom = 16.dp
                ), verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.Top)
        ) {
            val latestVersion = latestRelease.data
            val isLatest = if (latestVersion == null) { true } else {
                "v${currentVersion.tagName}" == latestVersion.tagName
            }

            CurrentVersionItem(
                currentVersion = currentVersion.tagName
            )
            CheckUpdateSection(
                releaseState = latestRelease,
                isLatest = isLatest,
                onButtonClick = { aboutViewModel.checkForUpdates() },
                onDownloadClick = { url ->
                    WebIntent.openUrlCustomTab(context, url)
                },
                onShowBottomSheet = { showBottomSheet.value = true }
            )
            Section(
                items = listOf(
                    SectionItem.General(
                        icon = IconResource.Drawable(R.drawable.ic_github),
                        title = stringResource(R.string.about_app_github_label),
                        subtitle = stringResource(R.string.about_app_github_desc),
                        onClick = { WebIntent.openUrlCustomTab(context, BuildConfig.REPO_URL) }
                    ),
                    SectionItem.General(
                        icon = IconResource.Drawable(R.drawable.shiki_logo),
                        title = stringResource(R.string.about_app_shikimori_label),
                        subtitle = stringResource(R.string.about_app_shikimori_desc),
                        onClick = { WebIntent.openUrlCustomTab(context, BuildConfig.BASE_URL) }
                    ),
                    SectionItem.General(
                        icon = IconResource.Drawable(R.drawable.ic_mangadex_v2),
                        title = stringResource(R.string.about_app_mangadex_label),
                        subtitle = stringResource(R.string.about_app_mangadex_desc),
                        onClick = { WebIntent.openUrlCustomTab(context, BuildConfig.MANGADEX_BASE_URL) }
                    )
                )
            )
        }
    }

    latestRelease.data?.let { release ->
        ReleaseNotesBottomSheet(
            currentVersion = currentVersion.tagName,
            release = release,
            onDismiss = { showBottomSheet.value = false },
            onDownloadReleaseClick = {
                WebIntent.openUrlCustomTab(
                    context = context,
                    url = release.assets.first().downloadUrl
                )
            },
            showBottomSheet = showBottomSheet.value
        )
    }
}