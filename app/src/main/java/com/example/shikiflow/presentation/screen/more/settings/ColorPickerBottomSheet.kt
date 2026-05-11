package com.example.shikiflow.presentation.screen.more.settings

import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.style.ExperimentalFoundationStyleApi
import androidx.compose.foundation.style.MutableStyleState
import androidx.compose.foundation.style.styleable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogWindowProvider
import com.example.shikiflow.R
import com.example.shikiflow.presentation.common.SnapFlingLazyRow
import com.example.shikiflow.presentation.common.ignoreHorizontalParentPadding
import com.example.shikiflow.presentation.common.mappers.ColorMapper
import kotlin.math.abs

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationStyleApi::class)
@Composable
fun ColorPickerBottomSheet(
    currentColor: Color,
    useSystemWallpaperColor: Boolean,
    onDismiss: () -> Unit,
    onSave: (Color, Boolean) -> Unit
) {
    val horizontalPadding = 12.dp
    val styleState = remember { MutableStyleState(null) }
    var hue by remember {
        mutableFloatStateOf(currentColor.extractHue())
    }
    val saturation = 0.7f
    val lightness = 0.5f

    val selectedColor = remember(hue) {
        Color.hsl(hue, saturation, lightness)
    }
    var useSystemColor by remember {
        mutableStateOf(useSystemWallpaperColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
    }

    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )
    val outlineColor: @Composable (Boolean) -> Color = { boolean ->
        if(useSystemColor) {
            MaterialTheme.colorScheme.outline
        } else {
            when (boolean) {
                true -> MaterialTheme.colorScheme.onSurface
                false -> MaterialTheme.colorScheme.outlineVariant
            }
        }
    }

    ModalBottomSheet(
        sheetState = sheetState,
        onDismissRequest = onDismiss
    ) {
        (LocalView.current.parent as? DialogWindowProvider)?.window?.let { window ->
            LaunchedEffect(Unit) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    window.isNavigationBarContrastEnforced = false
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = horizontalPadding, vertical = 6.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.Start),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.color_picker_bottom_sheet_system_label),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                    Switch(
                        checked = useSystemColor,
                        onCheckedChange = { useSystemColor = !useSystemColor }
                    )
                }
            }

            SnapFlingLazyRow(
                modifier = Modifier
                    .ignoreHorizontalParentPadding(horizontalPadding)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = horizontalPadding),
                horizontalArrangement = Arrangement.spacedBy(6.dp, Alignment.Start)
            ) {
                items(ColorMapper.getPickerQuickColors()) { color ->
                    val isCurrent = remember(hue) {
                        abs(hue - color.extractHue()) < 0.5f
                    }
                    val borderWidth = if(isCurrent) 3.dp else 1.dp
                    val borderColor = outlineColor(isCurrent)

                    Box(
                        modifier = Modifier
                            .styleable(
                                styleState = styleState
                            ) {
                                size(40.dp)
                                shape(RoundedCornerShape(percent = 24))
                                border(
                                    width = borderWidth,
                                    color = borderColor
                                )
                                background(color)
                            }
                            .clickable(
                                enabled = !useSystemColor,
                                onClick = { hue = color.extractHue() }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        if(isCurrent) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                tint = borderColor,
                                contentDescription = "Current Primary Color",
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp)
                    .clip(RoundedCornerShape(percent = 24))
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = (0..360 step 8).map { hue ->
                                Color.hsl(hue.toFloat(), saturation, lightness)
                            }
                        )
                    )
                    .then(
                        if(useSystemColor) {
                            Modifier.border(
                                width = 3.dp,
                                color = MaterialTheme.colorScheme.outline,
                                shape = RoundedCornerShape(percent = 24)
                            )
                        } else Modifier
                    ),
                contentAlignment = Alignment.Center
            ) {
                Slider(
                    value = hue,
                    onValueChange = { hue = it },
                    enabled = !useSystemColor,
                    valueRange = 0f..360f,
                    colors = SliderDefaults.colors(
                        thumbColor = MaterialTheme.colorScheme.onSurface,
                        activeTrackColor = Color.Transparent,
                        inactiveTrackColor = Color.Transparent,
                        disabledActiveTrackColor = Color.Transparent,
                        disabledActiveTickColor = Color.Transparent,
                        disabledInactiveTickColor = Color.Transparent,
                        disabledInactiveTrackColor = Color.Transparent
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Button(
                    shape = RoundedCornerShape(percent = 24),
                    onClick = { onSave(selectedColor, useSystemColor) }
                ) {
                    Text(
                        text = stringResource(R.string.color_picker_bottom_sheet_save)
                    )
                }
            }
        }
    }
}

private fun Color.extractHue(): Float {
    val r = red
    val g = green
    val b = blue

    val max = maxOf(r, g, b)
    val min = minOf(r, g, b)
    val delta = max - min

    if (delta == 0f) return 0f

    val hue = when (max) {
        r -> 60f * (((g - b) / delta) % 6f)
        g -> 60f * (((b - r) / delta) + 2f)
        else -> 60f * (((r - g) / delta) + 4f)
    }

    return if (hue < 0) hue + 360f else hue
}