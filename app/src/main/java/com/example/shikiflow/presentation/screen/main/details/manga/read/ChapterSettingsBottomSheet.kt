package com.example.shikiflow.presentation.screen.main.details.manga.read

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.shikiflow.R
import com.example.shikiflow.presentation.viewmodel.SettingsViewModel
import com.example.shikiflow.utils.IconResource
import com.example.shikiflow.utils.toIcon

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChapterSettingsBottomSheet(
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    settingsViewModel: SettingsViewModel = hiltViewModel(),
) {
    val isDataSaverEnabled = settingsViewModel.isDataSaver.collectAsState().value
    val chapterUIMode = settingsViewModel.chapterUIMode.collectAsState().value

    val sheetState = rememberModalBottomSheetState()

    ModalBottomSheet(
        sheetState = sheetState,
        onDismissRequest = onDismiss,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp, alignment = Alignment.Top)
        ) {
            ChapterSettingsRow(
                title = stringResource(R.string.settings_app_ui_mode),
                currentValue = stringResource(chapterUIMode.displayValue),
                values = ChapterUIMode.entries.map { stringResource(it.displayValue)  },
                iconResources = ChapterUIMode.entries.map { it.icon },
                onSettingClick = { selectedMode ->
                    settingsViewModel.setChapterUIMode(ChapterUIMode.valueOf(selectedMode.uppercase()))
                }
            )
            ChapterSettingsRow(
                title = stringResource(R.string.settings_data_saver_mode),
                currentValue = if(isDataSaverEnabled) stringResource(R.string.settings_data_saver_mode_enabled)
                    else stringResource(R.string.settings_data_saver_mode_disabled),
                values = listOf(
                    stringResource(R.string.settings_data_saver_mode_enabled),
                    stringResource(R.string.settings_data_saver_mode_disabled)
                ),
                onSettingClick = { selectedValue ->
                    settingsViewModel.setDataSaver(!isDataSaverEnabled)
                }
            )
        }
    }
}

@Composable
private fun ChapterSettingsRow(
    title: String,
    currentValue: String,
    values: List<String>,
    onSettingClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    iconResources: List<IconResource>? = null,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(8.dp, alignment = Alignment.Top)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium
        )
        Row(
            modifier = modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.background)
        ) {
            values.forEach { settingValue ->
                ChapterSettingsItem(
                    title = settingValue,
                    icon = iconResources?.get(values.indexOf(settingValue)),
                    isChecked = settingValue == currentValue,
                    onClick = { onSettingClick(settingValue) },
                    modifier = Modifier
                        .weight(1f)
                        .padding(4.dp)
                )
            }
        }
    }
}

@Composable
private fun ChapterSettingsItem(
    title: String,
    icon: IconResource?,
    isChecked: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable { if (!isChecked) onClick() }
            .heightIn(min = 48.dp)
            .background(
                if (isChecked) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.background
            )
            .padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterVertically)
    ) {
        icon?.toIcon(
            modifier = Modifier.size(24.dp),
            tint = if (isChecked) MaterialTheme.colorScheme.onPrimary
                else MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium.copy(
                color = if (isChecked) MaterialTheme.colorScheme.onPrimary
                    else MaterialTheme.colorScheme.onSurface
            ),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}