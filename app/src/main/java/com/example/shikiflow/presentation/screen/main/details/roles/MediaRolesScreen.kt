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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.shikiflow.domain.model.auth.AuthType
import com.example.shikiflow.domain.model.common.MediaRolesType
import com.example.shikiflow.domain.model.common.RoleType
import com.example.shikiflow.presentation.screen.main.details.MediaNavOptions
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun MediaRolesScreen(
    id: Int,
    mediaRolesType: MediaRolesType,
    roleTypes: List<RoleType>,
    authType: AuthType,
    navOptions: MediaNavOptions
) {
    val typesList = roleTypes.sortedBy { it.ordinal }

    val scope = rememberCoroutineScope()
    val pagerState = rememberPagerState(
        initialPage = typesList.indexOf(roleTypes.first())
    ) { typesList.size }

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
        /*floatingActionButton = {
            if(authType == AuthType.ANILIST) {

            }
        }*/
    ) { paddingValues ->
        HorizontalPager(
            state = pagerState
        ) { page ->
            MediaRolesContent(
                id = id,
                mediaRolesType = mediaRolesType,
                roleType = typesList[page],
                navOptions = navOptions,
                paddingValues = paddingValues
            )
        }
    }
}