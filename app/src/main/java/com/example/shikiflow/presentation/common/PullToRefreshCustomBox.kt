package com.example.shikiflow.presentation.common

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults.Indicator
import androidx.compose.material3.pulltorefresh.PullToRefreshState
import androidx.compose.material3.pulltorefresh.pullToRefresh
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

//Added Custom Pull-to-refresh as for some reason official one doesn't have 'enabled' argument
@Composable
fun PullToRefreshCustomBox(
    isRefreshing: Boolean,
    modifier: Modifier = Modifier,
    state: PullToRefreshState = rememberPullToRefreshState(),
    onRefresh: () -> Unit,
    contentAlignment: Alignment = Alignment.TopStart,
    enabled: Boolean = true,
    indicator: @Composable BoxScope.() -> Unit = {
        Indicator(
            modifier = Modifier.align(Alignment.TopCenter),
            isRefreshing = isRefreshing,
            state = state,
        )
    },
    content: @Composable BoxScope.() -> Unit,
) {
    Box(
        modifier = modifier.pullToRefresh(
            state = state,
            isRefreshing = isRefreshing,
            onRefresh = onRefresh,
            enabled = enabled,
        ),
        contentAlignment = contentAlignment
    ) {
        content()
        indicator()
    }
}