package com.example.shikiflow.presentation.viewmodel.character.roles

import com.example.shikiflow.presentation.screen.main.details.MediaRolesType
import com.example.shikiflow.presentation.screen.main.details.RoleSort
import com.example.shikiflow.presentation.screen.main.details.RoleType

data class MediaRolesParams(
    val id: Int? = null,
    val typeSortMap: Map<RoleType, RoleSort>? = null,
    val mediaRolesType: MediaRolesType? = null
)
