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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.shikiflow.R
import com.example.shikiflow.domain.model.settings.MangaChapterSettings
import com.example.shikiflow.utils.IconResource
import com.example.shikiflow.utils.toIcon

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChapterSettingsBottomSheet(
    onDismiss: () -> Unit,
    mangaSettings: MangaChapterSettings,
    onSettingsChange: (MangaChapterSettings) -> Unit,
    modifier: Modifier = Modifier
) {
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
                currentValue = stringResource(mangaSettings.chapterUIMode.displayValue),
                values = ChapterUIMode.entries.map { stringResource(it.displayValue)  },
                iconResources = ChapterUIMode.entries.map { it.icon },
                onSettingClick = { selectedIndex ->
                    onSettingsChange(mangaSettings.copy(
                        chapterUIMode = ChapterUIMode.entries[selectedIndex]
                    ))
                }
            )
            ChapterSettingsRow(
                title = stringResource(R.string.settings_data_saver_mode),
                currentValue = if(mangaSettings.isDataSaverEnabled) stringResource(R.string.settings_data_saver_mode_enabled)
                    else stringResource(R.string.settings_data_saver_mode_disabled),
                values = listOf(
                    stringResource(R.string.settings_data_saver_mode_enabled),
                    stringResource(R.string.settings_data_saver_mode_disabled)
                ),
                onSettingClick = { selectedIndex ->
                    onSettingsChange(mangaSettings.copy(
                        isDataSaverEnabled = selectedIndex == 0)
                    )
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
    onSettingClick: (Int) -> Unit,
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
            values.forEachIndexed { index, settingValue ->
                ChapterSettingsItem(
                    title = settingValue,
                    icon = iconResources?.get(values.indexOf(settingValue)),
                    isChecked = settingValue == currentValue,
                    onClick = { onSettingClick(index) },
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