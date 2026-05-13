package com.example.shikiflow.presentation.screen.main

import androidx.compose.runtime.compositionLocalOf
import com.example.shikiflow.domain.model.media_details.PreferredTitleType

val LocalTitleTypeController = compositionLocalOf<PreferredTitleType> {
    error("TitleTypeController not provided")
}