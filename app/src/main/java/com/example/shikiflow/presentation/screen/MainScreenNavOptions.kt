package com.example.shikiflow.presentation.screen

import com.example.shikiflow.presentation.screen.main.details.DetailsNavRoute

interface MainScreenNavOptions : MainNavOptions {
    fun navigateToDetails(detailsNavRoute: DetailsNavRoute)
}