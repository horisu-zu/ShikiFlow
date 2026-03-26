package com.example.shikiflow.presentation.viewmodel.user.social

import com.example.shikiflow.domain.model.user.social.SocialCategory

data class SocialParams(
    val userId: Int? = null,
    val currentCategory: SocialCategory? = null
)
