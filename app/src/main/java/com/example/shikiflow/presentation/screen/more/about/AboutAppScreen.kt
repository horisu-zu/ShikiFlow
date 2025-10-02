package com.example.shikiflow.presentation.screen.more.about

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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
import com.example.shikiflow.BuildConfig
import com.example.shikiflow.R
import com.example.shikiflow.domain.model.common.SectionItem
import com.example.shikiflow.presentation.common.ErrorItem
import com.example.shikiflow.presentation.screen.more.Section
import com.example.shikiflow.presentation.viewmodel.AboutViewModel
import com.example.shikiflow.utils.IconResource
import com.example.shikiflow.utils.Resource
import com.example.shikiflow.utils.WebIntent

@Composable
fun AboutAppScreen(
    aboutViewModel: AboutViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val latestRelease = aboutViewModel.latestRelease
    val currentVersion by aboutViewModel.currentVersion.collectAsState()
    val showBottomSheet = remember { mutableStateOf(false) }

    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    top = innerPadding.calculateTopPadding() + 12.dp,
                    start = innerPadding.calculateStartPadding(LayoutDirection.Ltr),
                    end = innerPadding.calculateEndPadding(LayoutDirection.Ltr),
                ), verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.Top)
        ) {
            when(currentVersion) {
                is Resource.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) { CircularProgressIndicator() }
                }
                is Resource.Success -> {
                    val currentVersion = currentVersion.data

                    currentVersion?.let { version ->
                        CurrentVersionItem(
                            currentRelease = version,
                            modifier = Modifier
                        )
                    }
                    latestRelease?.let {
                        if(currentVersion != latestRelease) {
                            LatestReleaseItem(
                                latestRelease = latestRelease,
                                showBottomSheet = { showBottomSheet.value = true },
                                context = context,
                                modifier = Modifier
                            )
                        }
                    }
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
                is Resource.Error -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        ErrorItem(
                            message = currentVersion.message ?: "?",
                            buttonLabel = stringResource(id = R.string.common_retry),
                            onButtonClick = {
                                aboutViewModel.getLocalVersion()
                            }
                        )
                    }
                }
            }
        }
    }

    latestRelease?.let {
        ReleaseNotesBottomSheet(
            currentVersion = currentVersion.data?.tagName ?: stringResource(R.string.common_unknown),
            release = latestRelease,
            onDismiss = { showBottomSheet.value = false },
            onDownloadReleaseClick = {
                WebIntent.openUrlCustomTab(
                    context = context,
                    url = latestRelease.assets.first().downloadUrl
                )
            }, showBottomSheet = showBottomSheet.value
        )
    }
}