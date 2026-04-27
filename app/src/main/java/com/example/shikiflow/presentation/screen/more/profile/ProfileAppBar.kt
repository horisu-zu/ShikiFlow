package com.example.shikiflow.presentation.screen.more.profile

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import com.example.shikiflow.R
import com.example.shikiflow.domain.model.auth.AuthType
import com.example.shikiflow.domain.model.user.User
import com.example.shikiflow.presentation.common.DynamicTopAppBar
import com.example.shikiflow.presentation.common.image.BaseImage
import com.example.shikiflow.presentation.common.image.ImageType
import com.example.shikiflow.presentation.common.image.shimmerEffect
import com.example.shikiflow.utils.foregroundGradient

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileAppBar(
    userData: User?,
    authType: AuthType?,
    isCurrentUser: Boolean,
    scrollBehavior: TopAppBarScrollBehavior,
    statusBarsPadding: PaddingValues,
    navOptions: ProfileNavOptions,
    onToggleFollow: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colorScheme.background
) {
    DynamicTopAppBar(
        expandedHeight = 160.dp,
        collapsedHeight = 108.dp,
        scrollBehavior = scrollBehavior,
        modifier = modifier,
        backgroundColor = backgroundColor
    ) { offsetDp ->
        val maxOffsetDp = 52.dp
        val progress = (offsetDp / maxOffsetDp).coerceIn(0f, 1f)

        Box(modifier = Modifier.fillMaxSize()) {
            userData?.profileBannerUrl?.let { profileBanner ->
                BaseImage(
                    model = profileBanner,
                    contentScale = ContentScale.FillWidth,
                    imageType = ImageType.Screenshot(
                        width = Dp.Unspecified,
                        clip = RoundedCornerShape(0.dp)
                    ),
                    modifier = Modifier
                        .foregroundGradient(
                            gradientColors = listOf(
                                Color.Transparent,
                                backgroundColor
                            )
                        )
                )
            }
            userData?.let {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            top = statusBarsPadding.calculateTopPadding(),
                            start = 12.dp,
                            end = 12.dp
                        ),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    UserComponent(
                        userData = userData,
                        imageType = ImageType.Custom(
                            width = lerp(108.dp, 60.dp, progress),
                            aspectRatio = 1f,
                            clip = RoundedCornerShape(percent = 16)
                        ),
                        backgroundColor = backgroundColor,
                        isScrolled = scrollBehavior.state.collapsedFraction == 1f,
                        modifier = Modifier
                            .fillMaxHeight()
                            .weight(1f, fill = false)
                            .align(Alignment.Bottom)
                    )
                    if(isCurrentUser) {
                        Row(
                            modifier = Modifier.align(Alignment.Top),
                            horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.End)
                        ) {
                            IconButton(
                                onClick = { navOptions.navigateToSettings() },
                                colors = IconButtonDefaults.iconButtonColors(
                                    containerColor = backgroundColor.copy(alpha = 0.35f)
                                )
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Settings,
                                    contentDescription = "Settings"
                                )
                            }
                            IconButton(
                                onClick = { navOptions.navigateToAbout() },
                                colors = IconButtonDefaults.iconButtonColors(
                                    containerColor = backgroundColor.copy(alpha = 0.35f)
                                )
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Info,
                                    contentDescription = "About App"
                                )
                            }
                        }
                    } else if(authType == AuthType.ANILIST) {
                        UserFollowComponent(
                            isFollowing = userData.isFollowing,
                            modifier = Modifier
                                .align(Alignment.Bottom)
                                .clip(RoundedCornerShape(12.dp))
                                .background(MaterialTheme.colorScheme.primaryContainer)
                                .then(
                                    if (userData.isFollowing == null) {
                                        Modifier.shimmerEffect()
                                    } else Modifier.clickable { onToggleFollow(userData.isFollowing) }
                                )
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun UserComponent(
    userData: User,
    imageType: ImageType,
    backgroundColor: Color,
    isScrolled: Boolean,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.Start),
        verticalAlignment = Alignment.Bottom
    ) {
        BaseImage(
            model = userData.avatarUrl,
            contentDescription = "Avatar",
            imageType = imageType
        )

        Column(
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            if(userData.isFollower == true) {
                AnimatedVisibility(
                    visible = !isScrolled,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    Text(
                        text = stringResource(R.string.profile_follows_you),
                        style = MaterialTheme.typography.labelSmall,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier
                            .clip(RoundedCornerShape(percent = 16))
                            .background(MaterialTheme.colorScheme.surfaceContainer)
                            .padding(horizontal = 6.dp, vertical = 4.dp)
                    )
                }
            }

            Text(
                text = userData.nickname,
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

@Composable
private fun UserFollowComponent(
    isFollowing: Boolean?,
    modifier: Modifier = Modifier
) {
    val rotationState by animateFloatAsState(
        targetValue = if(isFollowing == true) 45f else 0f,
        animationSpec = MaterialTheme.motionScheme.defaultSpatialSpec()
    )

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.Start),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.Add,
            tint = MaterialTheme.colorScheme.onPrimaryContainer.copy(
                alpha = if(isFollowing == null) 0f else 1f
            ),
            contentDescription = null,
            modifier = Modifier.rotate(rotationState)
        )

        Text(
            text = when(isFollowing) {
                true -> stringResource(R.string.profile_unfollow)
                else -> stringResource(R.string.profile_follow)
            },
            style = MaterialTheme.typography.bodySmall.copy(
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(
                    alpha = if(isFollowing == null) 0f else 1f
                )
            )
        )
    }
}