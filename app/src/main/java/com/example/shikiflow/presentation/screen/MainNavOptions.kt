package com.example.shikiflow.presentation.screen

import com.example.shikiflow.domain.model.user.User
import com.example.shikiflow.presentation.screen.main.details.DetailsNavRoute

interface MainNavOptions : NavOptions {
    fun navigateToDetails(detailsNavRoute: DetailsNavRoute)

    fun navigateToProfile(user: User?)
}