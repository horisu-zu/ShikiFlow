package com.example.shikiflow.presentation.screen.more

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.example.shikiflow.R
import com.example.shikiflow.domain.model.common.SectionItem
import com.example.shikiflow.domain.model.user.User
import com.example.shikiflow.utils.IconResource

@Composable
fun MoreScreen(
    currentUser: User?,
    moreNavOptions: MoreNavOptions
) {
    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    top = innerPadding.calculateTopPadding() + 12.dp,
                    start = innerPadding.calculateStartPadding(LayoutDirection.Ltr) + 24.dp,
                    end = innerPadding.calculateEndPadding(LayoutDirection.Ltr) + 24.dp
                ),
            verticalArrangement = Arrangement.spacedBy(24.dp, Alignment.Top)
        ) {
            Section(
                items = listOf(
                    SectionItem.Expanded(
                        avatar = currentUser?.avatarUrl ?: "NoUrl?",
                        title = currentUser?.nickname
                            ?: stringResource(R.string.profile_screen_missing_nickname),
                        subtitle = stringResource(R.string.more_screen_profile),
                        onClick = { moreNavOptions.navigateToProfile() }
                    ),
                    SectionItem.General(
                        icon = IconResource.Drawable(R.drawable.ic_history),
                        title = stringResource(R.string.more_screen_history),
                        onClick = { moreNavOptions.navigateToHistory() }
                    )
                )
            )
            Section(
                items = listOf(
                    SectionItem.General(
                        icon = IconResource.Vector(Icons.Default.Settings),
                        title = stringResource(R.string.more_screen_settings),
                        onClick = { moreNavOptions.navigateToSettings() }
                    ),
                    SectionItem.General(
                        icon = IconResource.Vector(Icons.Default.Info),
                        title = stringResource(R.string.more_screen_about_app),
                        onClick = { moreNavOptions.navigateToAbout() }
                    )
                )
            )
        }
    }
}