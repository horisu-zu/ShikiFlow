package com.example.shikiflow.presentation.screen.more.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.shikiflow.R
import com.example.shikiflow.domain.model.user.User
import com.example.shikiflow.presentation.common.CustomDialog
import com.example.shikiflow.presentation.screen.main.MainTrackMode
import com.example.shikiflow.presentation.screen.main.details.manga.read.ChapterUIMode
import com.example.shikiflow.presentation.viewmodel.SettingsViewModel
import com.example.shikiflow.utils.AppUiMode
import com.example.shikiflow.utils.ThemeMode
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    userData: User,
    settingsViewModel: SettingsViewModel = hiltViewModel()
) {
    val context = LocalContext.current

    val settings by settingsViewModel.settings.collectAsStateWithLifecycle()
    val themeSettings by settingsViewModel.themeSettings.collectAsStateWithLifecycle()
    val mangaSettings by settingsViewModel.mangaSettings.collectAsStateWithLifecycle()

    val cacheSize by settingsViewModel.cacheSize.collectAsStateWithLifecycle()
    val openCacheDialog = remember { mutableStateOf(false) }

    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
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
                    top = innerPadding.calculateTopPadding(),
                    start = innerPadding.calculateStartPadding(LayoutDirection.Ltr) + 24.dp,
                    end = innerPadding.calculateEndPadding(LayoutDirection.Ltr) + 24.dp,
                    bottom = 12.dp
                ), verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.Top)
        ) {
            SettingsSection(
                title = stringResource(R.string.settings_account_section_title),
                items = listOf(
                    SectionItem.Image(
                        title = userData.nickname,
                        displayValue = stringResource(R.string.settings_sign_out),
                        imageUrl = userData.avatarUrl,
                        onClick = { settingsViewModel.logout() }
                    )
                )
            )
            SettingsSection(
                title = stringResource(R.string.settings_theme_section_title),
                items = listOf(
                    SectionItem.Mode(
                        title = stringResource(R.string.settings_app_theme),
                        mode = stringResource(themeSettings.themeMode.displayValue),
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
                        isChecked = themeSettings.isOledEnabled,
                        onClick = {
                            settingsViewModel.setOled(!themeSettings.isOledEnabled)
                        }
                    )
                )
            )
            SettingsSection(
                title = stringResource(R.string.seetings_interface_section_title),
                items = listOf(
                    SectionItem.Default(
                        title = stringResource(R.string.settings_track_mode),
                        displayValue = stringResource(settings.trackMode.displayValue),
                        onClick = {
                            bottomSheetConfig.value = BottomSheetConfig(
                                title = context.getString(R.string.settings_track_mode_select),
                                options = MainTrackMode.entries.map { context.getString(it.displayValue) },
                                currentValue = context.getString(settings.trackMode.displayValue),
                                onOptionClick = { selectedIndex ->
                                    settingsViewModel.setTrackMode(MainTrackMode.entries[selectedIndex])
                                    scope.launch { sheetState.hide() }.invokeOnCompletion {
                                        bottomSheetConfig.value = null
                                    }
                                }
                            )
                        }
                    ),
                    SectionItem.Default(
                        title = stringResource(R.string.settings_app_ui_mode),
                        displayValue = context.getString(settings.appUiMode.displayValue),
                        onClick = {
                            bottomSheetConfig.value = BottomSheetConfig(
                                title = context.getString(R.string.settings_app_mode_select),
                                options = AppUiMode.entries.map { context.getString(it.displayValue) },
                                currentValue = context.getString(settings.appUiMode.displayValue),
                                onOptionClick = { selectedIndex ->
                                    settingsViewModel.setAppUiMode(AppUiMode.entries[selectedIndex])
                                    scope.launch { sheetState.hide() }.invokeOnCompletion {
                                        bottomSheetConfig.value = null
                                    }
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
                        mode = stringResource(mangaSettings.chapterUIMode.displayValue),
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
                            settingsViewModel.setDataSaver(!mangaSettings.isDataSaverEnabled)
                        },
                        isChecked = mangaSettings.isDataSaverEnabled
                    )
                )
            )
        }
        bottomSheetConfig.value?.let { config ->
            SettingsBottomSheet(
                sheetState = sheetState,
                title = config.title,
                currentValue = config.currentValue,
                options = config.options,
                onOptionClick = config.onOptionClick,
                onDismiss = { bottomSheetConfig.value = null }
            )
        }
    }
}