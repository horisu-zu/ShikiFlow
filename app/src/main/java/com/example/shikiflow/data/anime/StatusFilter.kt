package com.example.shikiflow.data.anime

sealed class StatusFilter {
    data class Specific(val status: String) : StatusFilter()
    data object UserTracked : StatusFilter()
    data object All : StatusFilter()
}