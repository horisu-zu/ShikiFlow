package com.example.shikiflow.presentation.screen.more.settings

import android.util.Log
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
import androidx.compose.material3.HorizontalDivider
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
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.example.shikiflow.presentation.common.image.RoundedImage
import com.example.shikiflow.utils.ThemeMode
import com.example.shikiflow.utils.toIcon

@Composable
fun SettingsSection(
    modifier: Modifier = Modifier,
    title: String,
    items: List<SectionItem>
) {
    ConstraintLayout(
        modifier = modifier.fillMaxWidth()
            .padding(horizontal = 24.dp)
            .clip(RoundedCornerShape(12.dp))
            .border(1.dp, MaterialTheme.colorScheme.outline, shape = RoundedCornerShape(12.dp))
    ) {
        val (titleRef, itemsRef) = createRefs()

        Text(
            text = title,
            modifier = Modifier.constrainAs(titleRef) {
                top.linkTo(parent.top)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                width = Dimension.fillToConstraints
            }.padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 4.dp),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.secondary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        Column(
            modifier = Modifier.constrainAs(itemsRef) {
                top.linkTo(titleRef.bottom)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            }
        ) {
            items.fastForEachIndexed { index, item ->
                when(item) {
                    is SectionItem.Default -> {
                        TextItem(
                            title = item.title,
                            subtitle = item.displayValue,
                            onClick = item.onClick,
                            showDivider = index != items.lastIndex
                        )
                    }
                    is SectionItem.Image -> {
                        ImageItem(
                            title = item.title,
                            displayValue = item.displayValue,
                            imageUrl = item.imageUrl,
                            onClick = item.onClick,
                            showDivider = index != items.lastIndex
                        )
                    }
                    is SectionItem.Switch -> {
                        SwitchItem(
                            title = item.title,
                            displayValue = item.displayValue,
                            isChecked = item.isChecked,
                            onClick = item.onClick,
                            showDivider = index != items.lastIndex
                        )
                    }
                    is SectionItem.Theme -> {
                        ThemeItem(
                            title = item.title,
                            currentTheme = item.themeMode,
                            onClick = item.onClick
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TextItem(
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    showDivider: Boolean = true
) {
    ConstraintLayout(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        val (titleRef, subtitleRef, divider) = createRefs()

        Text(
            text = title,
            modifier = Modifier.constrainAs(titleRef) {
                top.linkTo(parent.top, margin = 4.dp)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                width = Dimension.fillToConstraints
            },
            style = MaterialTheme.typography.bodyMedium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        Text(
            text = subtitle,
            modifier = Modifier.constrainAs(subtitleRef) {
                top.linkTo(titleRef.bottom)
                bottom.linkTo(parent.bottom, margin = 4.dp)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                width = Dimension.fillToConstraints
            },
            style = MaterialTheme.typography.labelSmall.copy(
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f)
            ),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        if(showDivider) {
            HorizontalDivider(
                modifier = Modifier.constrainAs(divider) {
                    bottom.linkTo(parent.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    width = Dimension.fillToConstraints
                },
                color = MaterialTheme.colorScheme.background
            )
        }
    }
}

@Composable
private fun ImageItem(
    title: String,
    displayValue: String,
    imageUrl: String,
    onClick: () -> Unit,
    showDivider: Boolean = true
) {
    ConstraintLayout(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        val (titleRef, iconRef, valueRef, divider) = createRefs()

        RoundedImage(
            model = imageUrl,
            contentDescription = null,
            size = 36.dp,
            modifier = Modifier.constrainAs(iconRef) {
                top.linkTo(parent.top)
                bottom.linkTo(parent.bottom)
                start.linkTo(parent.start)
            }
        )

        Text(
            text = title,
            modifier = Modifier.constrainAs(titleRef) {
                top.linkTo(parent.top)
                bottom.linkTo(valueRef.top)
                start.linkTo(iconRef.end, margin = 16.dp)
                width = Dimension.fillToConstraints
            },
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        Text(
            text = displayValue,
            modifier = Modifier.constrainAs(valueRef) {
                top.linkTo(titleRef.bottom)
                bottom.linkTo(parent.bottom, margin = 4.dp)
                start.linkTo(iconRef.end, margin = 16.dp)
                end.linkTo(parent.end)
                width = Dimension.fillToConstraints
            },
            style = MaterialTheme.typography.labelSmall.copy(
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.75f)
            ),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        if(showDivider) {
            HorizontalDivider(
                modifier = Modifier.constrainAs(divider) {
                    bottom.linkTo(parent.bottom)
                    start.linkTo(titleRef.start)
                    end.linkTo(parent.end)
                    width = Dimension.fillToConstraints
                },
                color = MaterialTheme.colorScheme.background
            )
        }
    }
}

@Composable
private fun SwitchItem(
    title: String,
    displayValue: String,
    isChecked: Boolean,
    onClick: () -> Unit,
    showDivider: Boolean = true
) {
    Log.d("SwitchItem", "isChecked: $isChecked")
    ConstraintLayout(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        val (titleRef, valueRef, switchRef, divider) = createRefs()

        Text(
            text = title,
            modifier = Modifier.constrainAs(titleRef) {
                top.linkTo(parent.top)
                bottom.linkTo(valueRef.top)
                start.linkTo(parent.start)
                end.linkTo(switchRef.start, margin = 4.dp)
                width = Dimension.fillToConstraints
            },
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        Text(
            text = displayValue,
            modifier = Modifier.constrainAs(valueRef) {
                top.linkTo(titleRef.bottom)
                bottom.linkTo(parent.bottom)
                start.linkTo(parent.start)
                end.linkTo(switchRef.start, margin = 4.dp)
                width = Dimension.fillToConstraints
            },
            style = MaterialTheme.typography.labelSmall.copy(
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.75f)
            ),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        Switch(
            checked = isChecked,
            onCheckedChange = { onClick() },
            modifier = Modifier.constrainAs(switchRef) {
                top.linkTo(parent.top)
                bottom.linkTo(parent.bottom)
                end.linkTo(parent.end)
            }
        )

        if(showDivider) {
            HorizontalDivider(
                modifier = Modifier.constrainAs(divider) {
                    bottom.linkTo(parent.bottom)
                    start.linkTo(titleRef.start)
                    end.linkTo(parent.end)
                    width = Dimension.fillToConstraints
                },
                color = MaterialTheme.colorScheme.background
            )
        }
    }
}

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
}