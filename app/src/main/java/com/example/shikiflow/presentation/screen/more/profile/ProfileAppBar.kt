package com.example.shikiflow.presentation.screen.more.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import com.example.shikiflow.domain.model.user.User
import com.example.shikiflow.presentation.common.DynamicTopAppBar
import com.example.shikiflow.presentation.common.image.BaseImage
import com.example.shikiflow.presentation.common.image.GradientImage
import com.example.shikiflow.presentation.common.image.ImageType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileAppBar(
    userData: User?,
    scrollBehavior: TopAppBarScrollBehavior,
    statusBarsPadding: PaddingValues,
    modifier: Modifier = Modifier
) {
    val backgroundColor = MaterialTheme.colorScheme.surfaceVariant

    DynamicTopAppBar(
        expandedHeight = 144.dp,
        collapsedHeight = 92.dp,
        scrollBehavior = scrollBehavior,
        modifier = modifier,
        backgroundColor = backgroundColor
    ) { offsetDp ->
        val maxOffsetDp = 52.dp
        val progress = (offsetDp / maxOffsetDp).coerceIn(0f, 1f)

        val avatarImageType = ImageType.Custom(
            defaultWidth = lerp(96.dp, 48.dp, progress),
            defaultAspectRatio = 1f,
            defaultClip = RoundedCornerShape(percent = 12)
        )

        Box(modifier = Modifier.fillMaxSize()) {
            userData?.profileBannerUrl?.let { profileBanner ->
                GradientImage(
                    model = profileBanner,
                    contentScale = ContentScale.FillWidth,
                    imageType = ImageType.Screenshot(
                        defaultWidth = 0.dp,
                        defaultClip = RoundedCornerShape(0.dp)
                    ),
                    gradientFraction = 1f,
                    gradientColors = listOf(
                        Color.Transparent,
                        backgroundColor
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }
            Row(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(
                        top = statusBarsPadding.calculateTopPadding(),
                        start = 16.dp,
                        end = 16.dp,
                        bottom = 4.dp
                    ),
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.Start),
                verticalAlignment = Alignment.Bottom
            ) {
                BaseImage(
                    model = userData?.avatarUrl,
                    contentDescription = "Avatar",
                    imageType = avatarImageType
                )
                Text(
                    text = userData?.nickname ?: "",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.SemiBold
                    ),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .clip(RoundedCornerShape(percent = 24))
                        .background(backgroundColor.copy(alpha = 0.35f))
                        .padding(horizontal = 6.dp, vertical = 4.dp)
                )
            }
        }
    }
}