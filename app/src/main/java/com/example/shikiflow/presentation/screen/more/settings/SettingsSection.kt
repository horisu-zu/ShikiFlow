package com.example.shikiflow.presentation.screen.more.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEachIndexed
import com.example.shikiflow.presentation.common.image.RoundedImage
import com.example.shikiflow.utils.IconResource
import com.example.shikiflow.utils.toIcon

@Composable
fun SettingsSection(
    modifier: Modifier = Modifier,
    title: String,
    items: List<SectionItem>
) {
    Column(
        modifier = modifier.fillMaxWidth()
            //.padding(horizontal = 24.dp)
            .clip(RoundedCornerShape(12.dp))
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                shape = RoundedCornerShape(12.dp)
            )
    ) {
        Text(
            text = title,
            modifier = Modifier.padding(
                start = 16.dp,
                end = 16.dp,
                top = 8.dp,
                bottom = 4.dp
            ),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.secondary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        items.fastForEachIndexed { index, item ->
            when(item) {
                is SectionItem.Default -> {
                    TextItem(
                        title = item.title,
                        subtitle = item.displayValue,
                        onClick = item.onClick
                    )
                }
                is SectionItem.Image -> {
                    ImageItem(
                        title = item.title,
                        displayValue = item.displayValue,
                        imageUrl = item.imageUrl,
                        onClick = item.onClick
                    )
                }
                is SectionItem.Switch -> {
                    SwitchItem(
                        title = item.title,
                        displayValue = item.displayValue,
                        isChecked = item.isChecked,
                        onClick = item.onClick
                    )
                }
                is SectionItem.Mode -> {
                    ModeItem(
                        title = item.title,
                        entries = item.entries,
                        iconResources = item.iconResources,
                        weights = item.weights,
                        mode = item.mode,
                        onClick = item.onClick
                    )
                }
            }
        }
    }
}

@Composable
private fun TextItem(
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            text = subtitle,
            modifier = Modifier,
            style = MaterialTheme.typography.labelSmall.copy(
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.75f)
            ),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun ImageItem(
    title: String,
    displayValue: String,
    imageUrl: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RoundedImage(
            model = imageUrl,
            contentDescription = null,
            modifier = Modifier.size(24.dp),
        )
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                text = title,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = displayValue,
                style = MaterialTheme.typography.labelSmall.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.75f)
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun SwitchItem(
    title: String,
    displayValue: String,
    isChecked: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = displayValue,
                style = MaterialTheme.typography.labelSmall.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.75f)
                ),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
        Switch(
            checked = isChecked,
            onCheckedChange = { onClick() }
        )
    }
}

@Composable
private fun <T> ModeItem(
    title: String,
    entries: List<T>,
    iconResources: List<IconResource> = emptyList(),
    weights: List<Float> = emptyList(),
    mode: T,
    onClick: (T) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp, Alignment.Top)
    ) {
        Text(
            text = title,
            modifier = Modifier.fillMaxWidth(),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            entries.forEachIndexed { index, entry ->
                val entryWeight = weights.getOrNull(index) ?: 1f
                ModeRowItem(
                    entry = entry.toString(),
                    isCurrentMode = mode == entry,
                    onClick = { onClick(entry) },
                    modifier = Modifier.weight(entryWeight),
                    iconResource = iconResources.getOrNull(entries.indexOf(entry))
                )
            }
        }
    }
}

@Composable
private fun ModeRowItem(
    entry: String,
    isCurrentMode: Boolean,
    onClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    iconResource: IconResource? = null,
) {
    Column(
        modifier = modifier.clip(RoundedCornerShape(12.dp))
            .clickable { onClick(entry) }
            .background(if(isCurrentMode) MaterialTheme.colorScheme.inversePrimary else MaterialTheme.colorScheme.background)
            .border(
                width = if(isCurrentMode) 1.dp else 0.dp,
                color = if(isCurrentMode) MaterialTheme.colorScheme.inversePrimary else MaterialTheme.colorScheme.outline,
                shape = RoundedCornerShape(12.dp)
            ).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        iconResource?.let {
            iconResource.toIcon(
                modifier = Modifier.size(24.dp),
                tint = MaterialTheme.colorScheme.onSurface
            )
        }
        Text(
            text = entry,
            style = MaterialTheme.typography.bodyMedium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

/*
@Composable
private fun ThemeItem(
    title: String,
    currentTheme: ThemeMode,
    onClick: (ThemeMode) -> Unit
) {
    ConstraintLayout(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        val (titleRef, themeRow) = createRefs()

        Text(
            text = title,
            modifier = Modifier.constrainAs(titleRef) {
                top.linkTo(parent.top)
                bottom.linkTo(themeRow.top)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                width = Dimension.fillToConstraints
            },
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        Row(
            modifier = Modifier.constrainAs(themeRow) {
                top.linkTo(titleRef.bottom, margin = 4.dp)
                bottom.linkTo(parent.bottom)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                width = Dimension.fillToConstraints
            },
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ThemeMode.entries.forEach { theme ->
                ThemeRowItem(
                    themeMode = theme,
                    isCurrentTheme = currentTheme == theme,
                    onClick = { onClick(theme) },
                    modifier = Modifier
                        .weight(if(theme == ThemeMode.SYSTEM) 3f else 2f)
                        .padding(4.dp)
                )
            }
        }
    }
}

@Composable
private fun ThemeRowItem(
    themeMode: ThemeMode,
    isCurrentTheme: Boolean,
    onClick: (ThemeMode) -> Unit,
    modifier: Modifier
) {
    Column(
        modifier = modifier.clip(RoundedCornerShape(12.dp))
            .clickable { onClick(themeMode) }
            .background(if(isCurrentTheme) MaterialTheme.colorScheme.inversePrimary else MaterialTheme.colorScheme.background)
            .border(
                width = if(isCurrentTheme) 1.dp else 0.dp,
                color = if(isCurrentTheme) MaterialTheme.colorScheme.inversePrimary else MaterialTheme.colorScheme.outline,
                shape = RoundedCornerShape(12.dp)
            ).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        themeMode.icon.toIcon(
            modifier = Modifier.size(24.dp),
            tint = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = themeMode.displayValue,
            style = MaterialTheme.typography.bodyMedium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}*/
