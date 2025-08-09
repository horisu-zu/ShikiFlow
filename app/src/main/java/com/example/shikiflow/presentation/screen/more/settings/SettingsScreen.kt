package com.example.shikiflow.presentation.screen.more.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.graphql.CurrentUserQuery
import com.example.shikiflow.presentation.common.CustomDialog
import com.example.shikiflow.presentation.screen.main.details.manga.read.ChapterUIMode
import com.example.shikiflow.presentation.viewmodel.SettingsViewModel
import com.example.shikiflow.utils.ThemeMode

@Composable
fun SettingsScreen(
    userData: CurrentUserQuery.Data?,
    settingsViewModel: SettingsViewModel = hiltViewModel()
) {
    val themeMode = settingsViewModel.appTheme.collectAsState().value
    val isOLEDModeEnabled = settingsViewModel.isOLEDModeEnabled.collectAsState().value
    val isDataSaverEnabled = settingsViewModel.isDataSaver.collectAsState().value
    val chapterUIMode = settingsViewModel.chapterUIMode.collectAsState().value
    val cacheSize = settingsViewModel.cacheSize.collectAsState().value

    val openCacheDialog = remember { mutableStateOf(false) }

    LaunchedEffect(userData) {
        settingsViewModel.loadCacheSize()
    }

    if(openCacheDialog.value) {
        CustomDialog(
            onDismissRequest = { openCacheDialog.value = false },
            text = "Are you sure you want to clear the image cache?",
            confirmButtonText = "Clear",
            onConfirm = {
                settingsViewModel.clearCache()
            }
        )
    }

    Scaffold { innerPadding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(
                top = innerPadding.calculateTopPadding() + 12.dp,
                start = innerPadding.calculateStartPadding(LayoutDirection.Ltr),
                end = innerPadding.calculateEndPadding(LayoutDirection.Ltr),
            ), verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.Top)
        ) {
            SettingsSection(
                title = "Account",
                items = listOf(
                    SectionItem.Image(
                        title = userData?.currentUser?.nickname ?: "Unknown",
                        displayValue = "Sign Out",
                        imageUrl = userData?.currentUser?.avatarUrl ?: "Unknown",
                        onClick = { settingsViewModel.logout() }
                    )
                )
            )
            SettingsSection(
                title = "Theme",
                items = listOf(
                    SectionItem.Mode(
                        title = "Application Theme",
                        mode = themeMode.displayValue,
                        entries = ThemeMode.entries.map { it.displayValue },
                        iconResources = ThemeMode.entries.map { it.icon },
                        onClick = { newTheme ->
                            settingsViewModel.setTheme(ThemeMode.valueOf(newTheme.uppercase()))
                        }
                    ),
                    SectionItem.Switch(
                        title = "OLED theme",
                        displayValue = "Enable OLED mode for darker backgrounds",
                        isChecked = isOLEDModeEnabled,
                        onClick = {
                            settingsViewModel.setOled(!isOLEDModeEnabled)
                        }
                    )
                )
            )
            SettingsSection(
                title = "Data",
                items = listOf(
                    SectionItem.Default(
                        title = "Clear Image Cache",
                        displayValue = "Cache Size: $cacheSize",
                        onClick = { if(cacheSize != "0 B") openCacheDialog.value = true }
                    )
                )
            )
            SettingsSection(
                title = "Manga",
                items = listOf(
                    SectionItem.Mode(
                        title = "Chapter UI Mode",
                        mode = chapterUIMode.displayValue,
                        entries = ChapterUIMode.entries.map { it.displayValue },
                        //iconResources = ChapterUIMode.entries.map { it.icon },
                        onClick = { newMode ->
                            settingsViewModel.setChapterUIMode(ChapterUIMode.valueOf(newMode.uppercase()))
                        }
                    ),
                    SectionItem.Switch(
                        title = "Data Saver Mode",
                        displayValue = "Reduce data usage by viewing lower quality versions of chapters",
                        onClick = {
                            settingsViewModel.setDataSaver(!isDataSaverEnabled)
                        },
                        isChecked = isDataSaverEnabled
                    )
                )
            )
        }
    }
}