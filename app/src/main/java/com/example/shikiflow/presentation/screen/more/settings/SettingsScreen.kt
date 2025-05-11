package com.example.shikiflow.presentation.screen.more.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.graphql.CurrentUserQuery
import com.example.shikiflow.presentation.viewmodel.SettingsViewModel

@Composable
fun SettingsScreen(
    userData: CurrentUserQuery.Data?,
    settingsViewModel: SettingsViewModel = hiltViewModel()
) {
    val themeMode = settingsViewModel.appTheme.collectAsState().value
    val isOLEDModeEnabled = settingsViewModel.isOLEDModeEnabled.collectAsState().value

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
                    SectionItem.Theme(
                        title = "Application Theme",
                        themeMode = themeMode,
                        onClick = { newTheme ->
                            settingsViewModel.setTheme(newTheme)
                        }
                    ),
                    SectionItem.Switch(
                        title = "OLED theme",
                        displayValue = "Fully black theme",
                        isChecked = isOLEDModeEnabled,
                        onClick = {
                            settingsViewModel.setOled(!isOLEDModeEnabled)
                        }
                    )
                )
            )
        }
    }
}