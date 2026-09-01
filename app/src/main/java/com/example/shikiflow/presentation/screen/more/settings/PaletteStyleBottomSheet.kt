package com.example.shikiflow.presentation.screen.more.settings

import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogWindowProvider
import com.example.shikiflow.R
import com.example.shikiflow.domain.model.settings.ThemeSettings
import com.example.shikiflow.presentation.common.ProgressBar
import com.example.shikiflow.presentation.common.SnapFlingLazyRow
import com.example.shikiflow.presentation.common.ignoreHorizontalParentPadding
import com.example.shikiflow.utils.ThemeMode.Companion.isDarkTheme
import com.materialkolor.PaletteStyle
import com.materialkolor.dynamiccolor.ColorSpec
import com.materialkolor.rememberDynamicColorScheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaletteStyleBottomSheet(
    themeSettings: ThemeSettings,
    onStyleChange: (PaletteStyle) -> Unit,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        dragHandle = null,
        sheetGesturesEnabled = false //Temporary fix found on issue tracker
    ) {
        val horizontalPadding = 16.dp
        val isDarkTheme = themeSettings.themeMode.isDarkTheme(isSystemInDarkTheme())

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
                .padding(horizontal = horizontalPadding, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = stringResource(R.string.settings_palette_style_bottom_title),
                style = MaterialTheme.typography.titleMedium
            )

            SnapFlingLazyRow(
                modifier = Modifier
                    .ignoreHorizontalParentPadding(horizontalPadding)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = horizontalPadding),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(PaletteStyle.entries) { style ->
                    ThemePreviewItem(
                        paletteStyle = style,
                        themeSettings = themeSettings,
                        isDarkTheme = isDarkTheme,
                        onClick = { onStyleChange(style) }
                    )
                }
            }
        }
    }
}

@Composable
private fun ThemePreviewItem(
    paletteStyle: PaletteStyle,
    themeSettings: ThemeSettings,
    isDarkTheme: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val paletteColorScheme = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && themeSettings.useSystemWallpaperColor) {
        val context = LocalContext.current
        val colors = if (isDarkTheme) dynamicDarkColorScheme(context)
            else dynamicLightColorScheme(context)

        rememberDynamicColorScheme(
            primary = colors.primary,
            isDark = isDarkTheme,
            isAmoled = themeSettings.isOledEnabled,
            style = paletteStyle,
            specVersion = ColorSpec.SpecVersion.SPEC_2025
        )
    } else {
        rememberDynamicColorScheme(
            seedColor = themeSettings.primaryColor,
            isDark = isDarkTheme,
            isAmoled = themeSettings.isOledEnabled,
            style = paletteStyle,
            specVersion = ColorSpec.SpecVersion.SPEC_2025
        )
    }

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() },
        verticalArrangement = Arrangement.spacedBy(4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        PaletteStylePreviewItem(
            paletteScheme = paletteColorScheme
        )

        Text(
            text = paletteStyle.name,
            style = MaterialTheme.typography.bodyMedium
        )

        RadioButton(
            selected = paletteStyle == themeSettings.paletteStyle,
            onClick = onClick
        )
    }
}

@Composable
private fun PaletteStylePreviewItem(
    paletteScheme: ColorScheme,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .width(108.dp)
            .aspectRatio(2f / 2.85f)
            .background(
                color = paletteScheme.background,
                shape = RoundedCornerShape(8.dp)
            )
            .padding(horizontal = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.Top)
    ) {
        //Main Tab Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
        ) {
            repeat(3) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .padding(horizontal = 4.dp)
                            .background(
                                color = paletteScheme.primary,
                                shape = RoundedCornerShape(percent = 24)
                            )
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .padding(horizontal = 4.dp)
                            .background(
                                color = paletteScheme.primary,
                                shape = RoundedCornerShape(
                                    topStartPercent = 32,
                                    topEndPercent = 32
                                )
                            )
                    )
                }
            }
        }

        //Track Items
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            repeat(3) { index ->
                val indexValue = index % 2 + 1

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.Start),
                    verticalAlignment = Alignment.Top
                ) {
                    Box(
                        modifier = Modifier
                            .width(16.dp)
                            .aspectRatio(2f / 2.85f)
                            .background(
                                color = paletteScheme.primaryContainer,
                                shape = RoundedCornerShape(percent = 12)
                            )
                    )

                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(top = 1.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp, Alignment.Top)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(fraction = 0.35f * indexValue)
                                .height(2.dp)
                                .background(
                                    color = paletteScheme.onBackground,
                                    shape = RoundedCornerShape(percent = 32)
                                )
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.Start),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            repeat(3) {
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(1.dp)
                                        .background(
                                            color = paletteScheme.onBackground,
                                            shape = RoundedCornerShape(percent = 32)
                                        )
                                )
                            }
                        }

                        ProgressBar(
                            progress = 1f - indexValue * 0.25f,
                            backgroundColor = paletteScheme.surfaceContainer,
                            progressColor = paletteScheme.primary,
                            height = 1.dp,
                            cornerRadius = 1.dp,
                            gapSize = 2.dp
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        //Bottom Nav Bar
        Row(
            modifier = Modifier
                .ignoreHorizontalParentPadding(8.dp)
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surfaceContainer)
                .padding(vertical = 4.dp),
            verticalAlignment = Alignment.Bottom
        ) {
            repeat(3) { index ->
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    if (index == 0) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .padding(horizontal = 6.dp)
                                .background(
                                    color = paletteScheme.secondaryContainer,
                                    shape = CircleShape
                                )
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .padding(horizontal = 8.dp)
                                .background(
                                    color = paletteScheme.onSurfaceVariant,
                                    shape = RoundedCornerShape(percent = 32)
                                )
                        )
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(2.dp)
                            .padding(horizontal = 8.dp)
                            .background(
                                color = paletteScheme.onSurfaceVariant,
                                shape = RoundedCornerShape(percent = 24)
                            )
                    )
                }
            }
        }
    }
}