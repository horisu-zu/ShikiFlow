package com.example.shikiflow.presentation.screen.more

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.example.shikiflow.R
import com.example.shikiflow.domain.model.common.SectionItem
import com.example.shikiflow.domain.model.user.User
import com.example.shikiflow.presentation.common.CustomSearchField
import com.example.shikiflow.presentation.common.ErrorItem
import com.example.shikiflow.presentation.common.image.RoundedImage
import com.example.shikiflow.presentation.viewmodel.more.MoreScreenViewModel
import com.example.shikiflow.presentation.viewmodel.more.UserSearchViewModel
import com.example.shikiflow.utils.Converter
import com.example.shikiflow.utils.IconResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoreScreen(
    currentUser: User,
    moreNavOptions: MoreNavOptions,
    moreScreenViewModel: MoreScreenViewModel = hiltViewModel()
) {
    val screenState by moreScreenViewModel.screenState.collectAsStateWithLifecycle()
    val searchQuery by moreScreenViewModel.searchQuery.collectAsStateWithLifecycle()

    val horizontalPadding = 24.dp

    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = {
                        CustomSearchField(
                            query = screenState.query,
                            label = stringResource(R.string.more_search_users),
                            onQueryChange = moreScreenViewModel::onQueryChange,
                            isActive = screenState.isSearchActive,
                            onActiveChange = moreScreenViewModel::onSearchActiveChange,
                            onExitSearch = moreScreenViewModel::exitSearchState,
                            modifier = Modifier.padding(
                                start = 8.dp,
                                end = horizontalPadding
                            ),
                            activeContainerColor = MaterialTheme.colorScheme.surface
                        )
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = if(screenState.isSearchActive) MaterialTheme.colorScheme.surface
                            else MaterialTheme.colorScheme.background
                    )
                )
                if(screenState.isSearchActive) HorizontalDivider()
            }
        }
    ) { innerPadding ->
        Crossfade(targetState = screenState.isSearchActive) { isSearchActive ->
            when(isSearchActive) {
                true -> {
                    MoreSearchContent(
                        query = searchQuery,
                        moreNavOptions = moreNavOptions,
                        modifier = Modifier.padding(
                            top = innerPadding.calculateTopPadding(),
                            start = innerPadding.calculateStartPadding(LayoutDirection.Ltr),
                            end = innerPadding.calculateEndPadding(LayoutDirection.Ltr)
                        )
                    )
                }
                false -> {
                    MoreMainContent(
                        currentUser = currentUser,
                        moreNavOptions = moreNavOptions,
                        modifier = Modifier.padding(
                            top = innerPadding.calculateTopPadding() + 12.dp,
                            start = innerPadding.calculateStartPadding(LayoutDirection.Ltr) + horizontalPadding,
                            end = innerPadding.calculateEndPadding(LayoutDirection.Ltr) + horizontalPadding
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun MoreMainContent(
    currentUser: User,
    moreNavOptions: MoreNavOptions,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(24.dp, Alignment.Top)
    ) {
        Section(
            items = listOf(
                SectionItem.Expanded(
                    avatar = currentUser.avatarUrl,
                    title = currentUser.nickname,
                    subtitle = stringResource(R.string.more_screen_profile),
                    onClick = { moreNavOptions.navigateToProfile(currentUser) }
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

@Composable
private fun MoreSearchContent(
    query: String,
    moreNavOptions: MoreNavOptions,
    modifier: Modifier = Modifier,
    userSearchViewModel: UserSearchViewModel = hiltViewModel()
) {
    val userSearchData = remember(query) {
        userSearchViewModel.paginatedUsers(query)
    }.collectAsLazyPagingItems()

    Box(modifier = modifier.fillMaxSize()) {
        if(userSearchData.loadState.refresh is LoadState.Loading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) { CircularProgressIndicator() }
        } else if(userSearchData.loadState.refresh is LoadState.Error) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                ErrorItem(
                    message = stringResource(R.string.common_error),
                    buttonLabel = stringResource(R.string.common_retry),
                    onButtonClick = { userSearchData.refresh() }
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.Top),
                contentPadding = PaddingValues(vertical = 12.dp)
            ) {
                items(
                    count = userSearchData.itemCount,
                    key = userSearchData.itemKey { it.id }
                ) { index ->
                    userSearchData[index]?.let { user ->
                        UserItem(
                            user = user,
                            onClick = { userId -> moreNavOptions.navigateToProfile(user) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun UserItem(
    user: User,
    onClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth()
            .clickable { onClick(user.id) }
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.Start),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RoundedImage(
            model = user.avatarUrl,
            size = 40.dp
        )
        Column(
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = user.nickname,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.SemiBold
                )
            )
            Text(
                text = stringResource(R.string.online_status, Converter.formatInstant(
                    instant = user.lastOnlineAt,
                    includeTime = true
                )),
                style = MaterialTheme.typography.bodySmall.copy(
                    fontWeight = FontWeight.Normal,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f)
                )
            )
        }
    }
}