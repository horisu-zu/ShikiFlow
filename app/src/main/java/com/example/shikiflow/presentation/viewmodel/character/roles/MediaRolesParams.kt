package com.example.shikiflow.presentation.viewmodel.character.roles

import com.example.shikiflow.domain.model.auth.AuthType
import com.example.shikiflow.presentation.screen.main.details.RoleType

data class MediaRolesParams(
    val roleTypes: List<RoleType>? = null,
    val authType: AuthType? = null
)
