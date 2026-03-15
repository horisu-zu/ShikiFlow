package com.example.shikiflow.presentation.screen.main.details.roles

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.shikiflow.R
import com.example.shikiflow.domain.model.auth.AuthType
import com.example.shikiflow.domain.model.sort.CharacterType
import com.example.shikiflow.domain.model.sort.MediaSort
import com.example.shikiflow.presentation.common.SortBottomSheet
import com.example.shikiflow.presentation.common.SortConfig
import com.example.shikiflow.presentation.screen.main.details.MediaRolesType
import com.example.shikiflow.presentation.screen.main.details.RoleType
import com.example.shikiflow.presentation.screen.main.details.MediaNavOptions
import com.example.shikiflow.presentation.screen.main.details.RoleSort
import com.example.shikiflow.presentation.viewmodel.character.MediaRolesViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun MediaRolesScreen(
    id: Int,
    mediaRolesType: MediaRolesType,
    roleTypes: List<RoleType>,
    authType: AuthType,
    navOptions: MediaNavOptions,
    mediaRolesViewModel: MediaRolesViewModel = hiltViewModel()
) {
    val typesList = roleTypes.sortedBy { it.ordinal }
    var showBottomSheet by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()
    val pagerState = rememberPagerState(
        initialPage = typesList.indexOf(roleTypes.first())
    ) { typesList.size }
    val currentType = typesList[pagerState.currentPage]

    val sortMap by mediaRolesViewModel.sortMap.collectAsStateWithLifecycle()

    LaunchedEffect(id) {
        mediaRolesViewModel.initializeSortMap(roleTypes, authType)
    }

    Scaffold(
        topBar = {
            PrimaryTabRow(
                selectedTabIndex = pagerState.currentPage,
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                indicator = {
                    TabRowDefaults.PrimaryIndicator(
                        modifier = Modifier
                            .tabIndicatorOffset(
                                selectedTabIndex = pagerState.currentPage,
                                matchContentSize = true
                            ),
                        width = Dp.Unspecified,
                        shape = RoundedCornerShape(topStart = 2.dp, topEnd = 2.dp),
                    )
                },
                divider = {
                    HorizontalDivider(
                        thickness = 1.dp,
                        color = MaterialTheme.colorScheme.surfaceBright
                    )
                },
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .windowInsetsPadding(WindowInsets.statusBars)
            ) {
                typesList.forEachIndexed { index, type ->
                    Tab(
                        selected = pagerState.currentPage == index,
                        onClick = {
                            scope.launch {
                                pagerState.animateScrollToPage(index)
                            }
                        },
                        text = {
                            Text(
                                text = stringResource(id = type.displayValue),
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold
                            )
                        },
                        modifier = Modifier.clip(RoundedCornerShape(8.dp))
                    )
                }
            }
        },
        floatingActionButton = {
            if(authType == AuthType.ANILIST) {
                FloatingActionButton(
                    onClick = { showBottomSheet = true },
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.onSurface
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_sort),
                        contentDescription = "Show Sort Bottom Sheet"
                    )
                }
            }
        }
    ) { paddingValues ->
        HorizontalPager(
            state = pagerState
        ) { page ->
            val roleType = typesList[page]
            val mediaRoles = mediaRolesViewModel.getMediaRoles(
                id = id,
                mediaRolesType = mediaRolesType,
                roleType = roleType
            ).collectAsLazyPagingItems()

            MediaRolesContent(
                roleType = roleType,
                mediaRoles = mediaRoles,
                navOptions = navOptions,
                paddingValues = paddingValues
            )
        }

        if(showBottomSheet) {
            val sortConfig = when(val roleSort = sortMap[currentType]) {
                is RoleSort.Media -> SortConfig(
                    options = MediaSort.Anilist.entries,
                    selected = roleSort.sort,
                    onSortChange = { sort ->
                        mediaRolesViewModel.setSort(currentType, RoleSort.Media(sort))
                    }
                )
                is RoleSort.VA -> SortConfig(
                    options = CharacterType.entries,
                    selected = roleSort.sort,
                    onSortChange = { sort ->
                        mediaRolesViewModel.setSort(currentType, RoleSort.VA(sort))
                    }
                )
                else -> null
            }

            sortConfig?.let {
                SortBottomSheet(
                    config = sortConfig,
                    onDismiss = { showBottomSheet = false }
                )
            }
        }
    }
}