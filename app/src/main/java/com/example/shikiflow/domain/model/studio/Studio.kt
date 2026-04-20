package com.example.shikiflow.domain.model.studio

data class Studio(
    val id: Int,
    val name: String,
    val isFavorite: Boolean? = null,
    val favorites: Int? = null
)
