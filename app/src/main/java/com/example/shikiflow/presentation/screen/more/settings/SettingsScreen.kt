package com.example.shikiflow.presentation.screen.more.settings

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.shikiflow.R
import com.example.shikiflow.domain.model.common.FileSize
import com.example.shikiflow.presentation.common.CustomDialog
import com.example.shikiflow.domain.model.settings.ChapterUIMode
import com.example.shikiflow.presentation.viewmodel.settings.SettingsViewModel
import com.example.shikiflow.domain.model.settings.AppUiMode
import com.example.shikiflow.domain.model.tracks.MediaType
import com.example.shikiflow.presentation.common.mappers.MediaTypeMapper.displayValue
import com.example.shikiflow.presentation.common.mappers.SettingsMapper.displayValue
import com.example.shikiflow.presentation.common.mappers.SettingsMapper.iconResource
import com.example.shikiflow.utils.IconResource
import com.example.shikiflow.utils.LocaleUtils
import com.example.shikiflow.utils.LocaleUtils.getAvailableLocales
import com.example.shikiflow.utils.ThemeMode
import com.example.shikiflow.utils.ThemeMode.Companion.isDarkTheme
import com.example.shikiflow.utils.WebIntent.openActionView
import com.materialkolor.PaletteStyle
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    settingsViewModel: SettingsViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val resources = LocalResources.current

    val settingsState by settingsViewModel.settingsState.collectAsStateWithLifecycle()
    val openCacheDialog = remember { mutableStateOf(false) }

    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    val bottomSheetConfig = remember { mutableStateOf<BottomSheetConfig?>(null) }

    val availableLocales = remember { context.getAvailableLocales() }
    var currentLocale by remember { mutableStateOf(LocaleUtils.getDefaultLocale()) }

    if(openCacheDialog.value) {
        CustomDialog(
            onDismissRequest = { openCacheDialog.value = false },
            text = stringResource(R.string.settings_cache_confirmation),
            confirmButtonText = stringResource(R.string.settings_cache_button_label),
            onConfirm = { settingsViewModel.clearCache() }
        )
    }

    LaunchedEffect(Unit) {
        settingsViewModel.loadCacheSize()
    }

    Scaffold { innerPadding ->
        settingsState.themeSettings?.let { themeSettings ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(
                        top = innerPadding.calculateTopPadding(),
                        start = 24.dp,
                        end = 24.dp,
                        bottom = 12.dp
                    ),
                verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.Top)
            ) {
                SettingsSection(
                    title = stringResource(R.string.settings_account_section_title),
                    items = listOfNotNull(
                        settingsState.user?.let { userData ->
                            SectionItem.User(
                                title = userData.nickname,
                                displayValue = stringResource(R.string.settings_sign_out),
                                authType = settingsState.authType,
                                imageUrl = userData.avatarUrl,
                                onClick = { settingsViewModel.logout() }
                            )
                        },
                        SectionItem.TrackerServices(
                            title = stringResource(R.string.settings_tracker_services_title),
                            currentAuthType = settingsState.authType,
                            serviceUpdateState = settingsState.settings.serviceUpdateState,
                            connectedServicesMap = settingsState.connectedServices,
                            onServiceClick = { authType, connectedUser ->
                                when(connectedUser) {
                                    true -> {
                                        settingsViewModel.clearUserData(authType)
                                    }
                                    false -> {
                                        val authUrl = settingsViewModel.getAuthorizationUrl(authType)

                                        context.openActionView(authUrl)
                                    }
                                }
                            },
                            onServiceUpdateToggle = {
                                settingsViewModel.setTrackerServiceUpdate(!settingsState.settings.serviceUpdateState)
                            }
                        )
                    )
                )
                SettingsSection(
                    title = stringResource(R.string.settings_theme_section_title),
                    items = listOf(
                        SectionItem.Mode(
                            title = stringResource(R.string.settings_dynamic_theme_label),
                            mode = when (themeSettings.isDynamicThemeEnabled) {
                                true -> stringResource(R.string.settings_enabled)
                                false -> stringResource(R.string.settings_disabled)
                            },
                            entries = listOf(
                                stringResource(R.string.settings_enabled),
                                stringResource(R.string.settings_disabled)
                            ),
                            iconResources = listOf(
                                IconResource.Drawable(resId = R.drawable.ic_palette),
                                IconResource.Drawable(resId = R.drawable.ic_format_paint)
                            ),
                            onClick = { index ->
                                settingsViewModel.setDynamicTheme(
                                    when(index) {
                                        0 -> true
                                        else -> false
                                    }
                                )
                            }
                        ),
                        SectionItem.Default(
                            title = stringResource(R.string.settings_palette_style_label),
                            displayValue = stringResource(R.string.settings_palette_style_desc),
                            isVisible = themeSettings.isDynamicThemeEnabled,
                            onClick = {
                                bottomSheetConfig.value = BottomSheetConfig(
                                    title = resources.getString(R.string.settings_palette_style_bottom_title),
                                    options = PaletteStyle.entries.map { it.name },
                                    currentValue = themeSettings.paletteStyle.name,
                                    onOptionClick = { selectedIndex ->
                                        settingsViewModel.setPaletteStyle(PaletteStyle.entries[selectedIndex])
                                        scope.launch { sheetState.hide() }.invokeOnCompletion {
                                            bottomSheetConfig.value = null
                                        }
                                    }
                                )
                            }
                        ),
                        SectionItem.Mode(
                            title = stringResource(R.string.settings_app_theme_label),
                            mode = stringResource(themeSettings.themeMode.displayValue()),
                            entries = ThemeMode.entries.map { stringResource(it.displayValue()) },
                            iconResources = ThemeMode.entries.map { it.iconResource() },
                            weights = listOf(3f, 2f, 2f),
                            onClick = { index ->
                                settingsViewModel.setTheme(ThemeMode.entries[index])
                            }
                        ),
                        SectionItem.Switch(
                            title = stringResource(R.string.settings_oled_theme),
                            displayValue = stringResource(R.string.settings_oled_desc),
                            isChecked = themeSettings.isOledEnabled,
                            isVisible = themeSettings.themeMode.isDarkTheme(isSystemInDarkTheme()),
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
                            title = stringResource(R.string.settings_language_label),
                            displayValue = availableLocales[currentLocale] ?: stringResource(R.string.settings_language_system),
                            onClick = {
                                bottomSheetConfig.value = BottomSheetConfig(
                                    title = resources.getString(R.string.settings_language_select),
                                    options = availableLocales.values.toList(),
                                    currentValue = availableLocales[currentLocale] ?: resources.getString(R.string.settings_language_system),
                                    onOptionClick = { selectedIndex ->
                                        currentLocale = availableLocales.keys.toList()[selectedIndex]
                                        LocaleUtils.setDefaultLocale(currentLocale)

                                        scope.launch { sheetState.hide() }.invokeOnCompletion {
                                            bottomSheetConfig.value = null
                                        }
                                    }
                                )
                            }
                        ),
                        SectionItem.Default(
                            title = stringResource(R.string.settings_track_mode),
                            displayValue = stringResource(settingsState.settings.trackMode.displayValue()),
                            onClick = {
                                bottomSheetConfig.value = BottomSheetConfig(
                                    title = resources.getString(R.string.settings_track_mode_select),
                                    options = MediaType.entries.map { resources.getString(it.displayValue()) },
                                    currentValue = resources.getString(settingsState.settings.trackMode.displayValue()),
                                    onOptionClick = { selectedIndex ->
                                        settingsViewModel.setTrackMode(MediaType.entries[selectedIndex])
                                        scope.launch { sheetState.hide() }.invokeOnCompletion {
                                            bottomSheetConfig.value = null
                                        }
                                    }
                                )
                            }
                        ),
                        SectionItem.Default(
                            title = stringResource(R.string.settings_app_ui_mode),
                            displayValue = stringResource(settingsState.settings.appUiMode.displayValue()),
                            onClick = {
                                bottomSheetConfig.value = BottomSheetConfig(
                                    title = resources.getString(R.string.settings_app_mode_select),
                                    options = AppUiMode.entries.map { resources.getString(it.displayValue()) },
                                    currentValue = resources.getString(settingsState.settings.appUiMode.displayValue()),
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
                            displayValue = buildString {
                                settingsState.cacheSize?.let { size ->
                                    append(stringResource(R.string.settings_cache_size))
                                    append(": ")
                                    append(
                                        when(size.unit) {
                                            FileSize.SizeUnit.B -> stringResource(R.string.cache_size_bytes, size.value.toLong())
                                            FileSize.SizeUnit.KB -> stringResource(R.string.cache_size_kbytes, size.value)
                                            FileSize.SizeUnit.MB -> stringResource(R.string.cache_size_mbytes, size.value)
                                            FileSize.SizeUnit.GB -> stringResource(R.string.cache_size_gbytes, size.value)
                                        }
                                    )
                                }
                            },
                            onClick = {
                                if(settingsState.cacheSize != FileSize(value = 0.0, unit = FileSize.SizeUnit.B))
                                    openCacheDialog.value = true
                            }
                        )
                    )
                )
                SettingsSection(
                    title = stringResource(R.string.media_type_manga),
                    items = listOf(
                        SectionItem.Mode(
                            title = stringResource(R.string.settings_chapter_ui_mode),
                            mode = stringResource(settingsState.mangaSettings.chapterUIMode.displayValue()),
                            entries = ChapterUIMode.entries.map { stringResource(it.displayValue()) },
                            iconResources = ChapterUIMode.entries.map { it.iconResource() },
                            onClick = { index ->
                                settingsViewModel.setChapterUIMode(ChapterUIMode.entries[index])
                            }
                        ),
                        SectionItem.Switch(
                            title = stringResource(R.string.settings_data_saver_mode),
                            displayValue = stringResource(R.string.settings_data_saver_desc),
                            onClick = {
                                settingsViewModel.setDataSaver(!settingsState.mangaSettings.isDataSaverEnabled)
                            },
                            isChecked = settingsState.mangaSettings.isDataSaverEnabled
                        ),
                        SectionItem.Switch(
                            title = stringResource(R.string.settings_update_progress_mode),
                            displayValue = stringResource(R.string.settings_update_progress_desc),
                            onClick = {
                                settingsViewModel.setTrackerChapterUpdate(!settingsState.mangaSettings.updateTrackProgress)
                            },
                            isChecked = settingsState.mangaSettings.updateTrackProgress
                        )
                    )
                )
            }
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