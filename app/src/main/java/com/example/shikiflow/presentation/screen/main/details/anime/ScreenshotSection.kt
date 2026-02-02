package com.example.shikiflow.presentation.screen.main.details.anime

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.shikiflow.R
import com.example.shikiflow.presentation.common.image.BaseImage
import com.example.shikiflow.presentation.common.image.ImageType
import com.example.shikiflow.utils.ignoreHorizontalParentPadding

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun ScreenshotSection(
    screenshots: List<String>,
    selectedIndex: Int?,
    sharedTransitionScope: SharedTransitionScope,
    onScreenshotClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
    horizontalPadding: Dp = 12.dp
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = stringResource(R.string.anime_details_screenshots),
            style = MaterialTheme.typography.titleMedium
        )
        LazyRow(
            modifier = Modifier.ignoreHorizontalParentPadding(horizontalPadding)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(horizontal = horizontalPadding)
        ) {
            itemsIndexed(screenshots) { index, screenshot ->
                AnimatedVisibility(
                    visible = index != selectedIndex,
                    enter = fadeIn(spring(stiffness = 800f)),
                    exit = fadeOut(spring(stiffness = 800f)),
                    modifier = Modifier.animateItem()
                ) {
                    with(sharedTransitionScope) {
                        BaseImage(
                            model = screenshot,
                            modifier = Modifier
                                .clickable { onScreenshotClick(index) }
                                .sharedElement(
                                    sharedContentState = rememberSharedContentState(key = "screenshot-$index"),
                                    animatedVisibilityScope = this@AnimatedVisibility
                                ),
                            imageType = ImageType.Screenshot()
                        )
                    }
                }
            }
        }
    }
}