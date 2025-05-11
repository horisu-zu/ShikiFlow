package com.example.shikiflow.presentation.screen.more

import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.graphql.CurrentUserQuery
import com.example.shikiflow.R
import com.example.shikiflow.data.common.SectionItem
import com.example.shikiflow.presentation.common.CustomSearchField
import com.example.shikiflow.presentation.viewmodel.SearchViewModel
import com.example.shikiflow.utils.IconResource

@Composable
fun MoreScreen(
    currentUser: CurrentUserQuery.Data?,
    moreNavOptions: MoreNavOptions,
    searchViewModel: SearchViewModel = hiltViewModel(),
) {
    val searchQuery by searchViewModel.screenState.collectAsState()
    val screenState by searchViewModel.screenState.collectAsState()

    Scaffold(
        topBar = {
            CustomSearchField(
                query = searchQuery.query,
                onQueryChange = searchViewModel::onQueryChange,
                isActive = screenState.isSearchActive,
                onActiveChange = searchViewModel::onSearchActiveChange,
                modifier = Modifier.padding(start = 24.dp, end = 24.dp, top = 32.dp, bottom = 12.dp)
            )
        }
    ) { innerPadding ->
        ConstraintLayout(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    top = innerPadding.calculateTopPadding(),
                    start = innerPadding.calculateStartPadding(LayoutDirection.Ltr),
                    end = innerPadding.calculateEndPadding(LayoutDirection.Ltr)
                )
        ) {
            val (userBlock, settingsBlock) = createRefs()

            Section(
                items = listOf(
                    SectionItem.Expanded(
                        avatar = currentUser?.currentUser?.avatarUrl ?: "NoUrl?",
                        title = currentUser?.currentUser?.nickname ?: "NoNickname!",
                        subtitle = "My Profile",
                        onClick = { moreNavOptions.navigateToProfile() }
                    ),
                    SectionItem.General(
                        icon = IconResource.Drawable(R.drawable.ic_group),
                        title = "Clubs",
                        onClick = { /**/ }
                    ),
                    SectionItem.General(
                        icon = IconResource.Drawable(R.drawable.ic_history),
                        title = "History",
                        onClick = { moreNavOptions.navigateToHistory() }
                    )
                ),
                modifier = Modifier.constrainAs(userBlock) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
            )
            Section(
                items = listOf(
                    SectionItem.General(
                        icon = IconResource.Vector(Icons.Default.Settings),
                        title = "Settings",
                        onClick = { moreNavOptions.navigateToSettings() }
                    ),
                    SectionItem.General(
                        icon = IconResource.Vector(Icons.Default.Info),
                        title = "About App",
                        onClick = { /**/ }
                    )
                ),
                modifier = Modifier.constrainAs(settingsBlock) {
                    top.linkTo(userBlock.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
            )
        }
    }
}