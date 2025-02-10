package com.example.shikiflow.presentation.screen.main.details.manga

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.graphql.CurrentUserQuery
import com.example.shikiflow.presentation.viewmodel.manga.MangaDetailsViewModel
import com.example.shikiflow.presentation.viewmodel.user.UserViewModel
import com.example.shikiflow.utils.Resource

@Composable
fun MangaDetailsScreen(
    id: String,
    currentUser: CurrentUserQuery.Data?,
    mangaDetailsViewModel: MangaDetailsViewModel = hiltViewModel(),
    userViewModel: UserViewModel = hiltViewModel()
) {
    val mangaDetails = mangaDetailsViewModel.mangaDetails.collectAsState()
    var isRefreshing by remember { mutableStateOf(false) }

    LaunchedEffect(id) {
        mangaDetailsViewModel.getMangaDetails(id)
    }

    Scaffold(
        topBar = {

        }
    ) { paddingValues ->
        when (mangaDetails.value) {
            is Resource.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            is Resource.Success -> {
                ConstraintLayout(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .verticalScroll(rememberScrollState())
                ) {
                    val (headerRef, descriptionRef) = createRefs()

                    MangaDetailsHeader(
                        mangaDetails = mangaDetails.value.data,
                        modifier = Modifier.constrainAs(headerRef) {
                            top.linkTo(parent.top)
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                            bottom.linkTo(descriptionRef.top)
                        }
                    )

                    MangaDetailsDesc(
                        mangaDetails = mangaDetails.value.data,
                        modifier = Modifier.constrainAs(descriptionRef) {
                            top.linkTo(headerRef.bottom, margin = 12.dp)
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                        }.padding(horizontal = 12.dp)
                    )
                }
            }
            is Resource.Error -> { /*TODO*/ }
        }
    }
}