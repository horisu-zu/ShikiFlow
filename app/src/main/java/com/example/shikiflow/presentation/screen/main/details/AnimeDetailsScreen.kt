package com.example.shikiflow.presentation.screen.main.details

import android.util.Log
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.shikiflow.presentation.common.FormattedText
import com.example.shikiflow.presentation.viewmodel.anime.AnimeDetailsViewModel
import com.example.shikiflow.utils.Converter
import com.example.shikiflow.utils.Resource

@Composable
fun AnimeDetailsScreen(
    id: String,
    animeDetailsViewModel: AnimeDetailsViewModel = hiltViewModel()
) {
    val animeDetails = animeDetailsViewModel.animeDetails.collectAsState()

    LaunchedEffect(id) {
        animeDetailsViewModel.getAnimeDetails(id)
    }

    Scaffold(
        topBar = {

        }
    ) { paddingValues ->
        when(animeDetails.value) {
            is Resource.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            is Resource.Success -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .verticalScroll(rememberScrollState())
                ) {
                    ConstraintLayout(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        val (titleRef, descriptionRef) = createRefs()

                        AnimeDetailsTitle(
                            animeDetails = animeDetails.value.data,
                            modifier = Modifier.constrainAs(titleRef) {
                                top.linkTo(parent.top)
                                start.linkTo(parent.start)
                                end.linkTo(parent.end)
                                bottom.linkTo(descriptionRef.top)
                            }
                        )

                        AnimeDetailsDesc(
                            animeDetails = animeDetails.value.data,
                            modifier = Modifier.constrainAs(descriptionRef) {
                                top.linkTo(titleRef.bottom, margin = 12.dp)
                            }.padding(horizontal = 12.dp)
                        )
                    }
                }
            }
            is Resource.Error -> { /*TODO*/ }
        }
    }
}