package com.example.shikiflow.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.shikiflow.utils.stretchOverscroll

@Composable
fun TestScreen() {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            //.stretchOverscroll()
            .padding(16.dp)
    ) {
        items(20) { index ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .background(Color.LightGray)
                    .padding(16.dp)
            ) {
                Text(text = "Item #$index")
            }
        }
    }
}