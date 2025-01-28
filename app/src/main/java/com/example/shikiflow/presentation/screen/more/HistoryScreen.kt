package com.example.shikiflow.presentation.screen.more

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.graphql.CurrentUserQuery
import com.example.shikiflow.presentation.viewmodel.user.UserHistoryViewModel

@Composable
fun HistoryScreen(
    userData: CurrentUserQuery.Data?,
    navController: NavController,
    userHistoryViewModel: UserHistoryViewModel = hiltViewModel()
) {
    val userHistoryData by userHistoryViewModel.userHistoryData.collectAsState()
    val isLoading by userHistoryViewModel.isLoading.collectAsState()
    val hasMorePages by userHistoryViewModel.hasMorePages.collectAsState()
    val currentUserId = userData?.currentUser?.id ?: ""
    val shouldLoadMore = remember {
        derivedStateOf {
            hasMorePages && !isLoading
        }
    }

    LaunchedEffect(currentUserId) {
        Log.d("HistoryScreen", "currentUserId: $currentUserId")
        if (currentUserId.isNotEmpty()) {
            userHistoryViewModel.loadUserHistory(currentUserId.toLong(), isRefresh = true)
        }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(userHistoryData.size) { index ->
            val historyItem = userHistoryData[index]
            HistoryItem(historyItem)

            if (index >= userHistoryData.size - 5 && shouldLoadMore.value) {
                userHistoryViewModel.loadUserHistory(currentUserId.toLong())
            }
        }

        if (isLoading) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }
    }
}