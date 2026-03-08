package com.example.shikiflow.presentation.screen.main

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.shikiflow.domain.model.mapper.UserRateMapper
import com.example.shikiflow.domain.model.track.UserRateStatus
import com.example.shikiflow.domain.model.tracks.MediaType

@Composable
fun MainTabRow(
    tabs: List<UserRateStatus>,
    mediaType: MediaType,
    selectedTab: Int,
    onTabSelected: (Int) -> Unit,
    isAtTop: Boolean,
) {
    PrimaryScrollableTabRow(
        selectedTabIndex = selectedTab,
        containerColor = if(isAtTop) MaterialTheme.colorScheme.background
            else MaterialTheme.colorScheme.surfaceVariant,
        edgePadding = 0.dp,
        indicator = {
            TabRowDefaults.PrimaryIndicator(
                Modifier.tabIndicatorOffset(selectedTab, matchContentSize = true),
                width = Dp.Unspecified,
                shape = RoundedCornerShape(topStart = 2.dp, topEnd = 2.dp),
            )
        },
        divider = { if(!isAtTop) HorizontalDivider() }
    ) {
        tabs.forEachIndexed { index, status ->
            val rateStatus = UserRateMapper.mapUserRateStatus(status, mediaType)

            Tab(
                selected = selectedTab == index,
                onClick = { onTabSelected(index) },
                text = {
                    Text(
                        text = stringResource(rateStatus),
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold
                    )
                }, modifier = Modifier.clip(RoundedCornerShape(8.dp))
            )
        }
    }
}