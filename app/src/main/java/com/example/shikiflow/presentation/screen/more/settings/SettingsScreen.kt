package com.example.shikiflow.presentation.screen.more.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.example.graphql.CurrentUserQuery
import com.example.shikiflow.R
import com.example.shikiflow.presentation.common.CustomDialog
import com.example.shikiflow.presentation.screen.main.MainTrackMode
import com.example.shikiflow.presentation.screen.main.details.manga.read.ChapterUIMode
import com.example.shikiflow.presentation.viewmodel.SettingsViewModel
import com.example.shikiflow.utils.AppUiMode
import com.example.shikiflow.utils.ThemeMode

@Composable
fun SettingsScreen(
    userData: CurrentUserQuery.Data?,
    settingsViewModel: SettingsViewModel = hiltViewModel()
) {
    val context = LocalContext.current

    val themeMode = settingsViewModel.appTheme.collectAsStateWithLifecycle().value
    val isOLEDModeEnabled = settingsViewModel.isOLEDModeEnabled.collectAsStateWithLifecycle().value
    val isDataSaverEnabled = settingsViewModel.isDataSaver.collectAsStateWithLifecycle().value
    val chapterUIMode = settingsViewModel.chapterUIMode.collectAsStateWithLifecycle().value
    val appUiMode = settingsViewModel.appUiMode.collectAsStateWithLifecycle().value
    val trackMode = settingsViewModel.trackMode.collectAsStateWithLifecycle().value
    val cacheSize = settingsViewModel.cacheSize.collectAsStateWithLifecycle().value

    val openCacheDialog = remember { mutableStateOf(false) }
    val bottomSheetConfig = remember { mutableStateOf<BottomSheetConfig?>(null) }

    LaunchedEffect(userData) {
        settingsViewModel.loadCacheSize()
    }

    if(openCacheDialog.value) {
        CustomDialog(
            onDismissRequest = { openCacheDialog.value = false },
            text = stringResource(R.string.settings_cache_confirmation),
            confirmButtonText = stringResource(R.string.settings_cache_button_label),
            onConfirm = { settingsViewModel.clearCache() }
        )
    }

    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(
                    top = innerPadding.calculateTopPadding() + 12.dp,
                    start = innerPadding.calculateStartPadding(LayoutDirection.Ltr),
                    end = innerPadding.calculateEndPadding(LayoutDirection.Ltr),
                    bottom = 12.dp
                ), verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.Top)
        ) {
            SettingsSection(
                title = stringResource(R.string.settings_account_section_title),
                items = listOf(
                    SectionItem.Image(
                        title = userData?.currentUser?.nickname ?: stringResource(R.string.common_unknown),
                        displayValue = stringResource(R.string.settings_sign_out),
                        imageUrl = userData?.currentUser?.avatarUrl ?: stringResource(R.string.common_unknown),
                        onClick = { settingsViewModel.logout() }
                    )
                )
            )
            SettingsSection(
                title = stringResource(R.string.settings_theme_section_title),
                items = listOf(
                    SectionItem.Mode(
                        title = stringResource(R.string.settings_app_theme),
                        mode = stringResource(themeMode.displayValue),
                        entries = ThemeMode.entries.map { stringResource(it.displayValue) },
                        iconResources = ThemeMode.entries.map { it.icon },
                        weights = listOf(3f, 2f, 2f),
                        onClick = { newTheme ->
                            settingsViewModel.setTheme(ThemeMode.valueOf(newTheme.uppercase()))
                        }
                    ),
                    SectionItem.Switch(
                        title = stringResource(R.string.settings_oled_theme),
                        displayValue = stringResource(R.string.settings_oled_desc),
                        isChecked = isOLEDModeEnabled,
                        onClick = {
                            settingsViewModel.setOled(!isOLEDModeEnabled)
                        }
                    )
                )
            )
            SettingsSection(
                title = stringResource(R.string.seetings_interface_section_title),
                items = listOf(
                    SectionItem.Default(
                        title = stringResource(R.string.settings_track_mode),
                        displayValue = stringResource(trackMode.displayValue),
                        onClick = {
                            bottomSheetConfig.value = BottomSheetConfig(
                                title = context.getString(R.string.settings_track_mode_select),
                                options = MainTrackMode.entries.map { context.getString(it.displayValue) },
                                currentValue = context.getString(trackMode.displayValue),
                                onOptionClick = { selectedIndex ->
                                    settingsViewModel.setTrackMode(MainTrackMode.entries[selectedIndex])
                                    bottomSheetConfig.value = null
                                }
                            )
                        }
                    ),
                    SectionItem.Default(
                        title = stringResource(R.string.settings_app_ui_mode),
                        displayValue = context.getString(appUiMode.displayValue),
                        onClick = {
                            bottomSheetConfig.value = BottomSheetConfig(
                                title = context.getString(R.string.settings_app_mode_select),
                                options = AppUiMode.entries.map { context.getString(it.displayValue) },
                                currentValue = context.getString(appUiMode.displayValue),
                                onOptionClick = { selectedIndex ->
                                    settingsViewModel.setAppUiMode(AppUiMode.entries[selectedIndex])
                                    bottomSheetConfig.value = null
                                }
                            )
                        }
                    )
                )
            )
            SettingsSection(
                title = stringResource(R.string.settings_data_section_title),
                items = listOf(
                    SectionItem.Default(
                        title = stringResource(R.string.settings_clear_cache),
                        displayValue = stringResource(R.string.settings_cache_size, cacheSize),
                        onClick = { if(cacheSize != context.getString(R.string.cache_size_zero_bytes)) openCacheDialog.value = true }
                    )
                )
            )
            SettingsSection(
                title = stringResource(R.string.settings_manga_section_title),
                items = listOf(
                    SectionItem.Mode(
                        title = stringResource(R.string.settings_chapter_ui_mode),
                        mode = stringResource(chapterUIMode.displayValue),
                        entries = ChapterUIMode.entries.map { stringResource(it.displayValue) },
                        iconResources = ChapterUIMode.entries.map { it.icon },
                        onClick = { newMode ->
                            settingsViewModel.setChapterUIMode(ChapterUIMode.valueOf(newMode.uppercase()))
                        }
                    ),
                    SectionItem.Switch(
                        title = stringResource(R.string.settings_data_saver_mode),
                        displayValue = stringResource(R.string.settings_data_saver_desc),
                        onClick = {
                            settingsViewModel.setDataSaver(!isDataSaverEnabled)
                        },
                        isChecked = isDataSaverEnabled
                    )
                )
            )
        }
        bottomSheetConfig.value?.let { config ->
            SettingsBottomSheet(
                title = config.title,
                currentValue = config.currentValue,
                options = config.options,
                onOptionClick = config.onOptionClick,
                onDismiss = { bottomSheetConfig.value = null }
            )
        }
    }
}