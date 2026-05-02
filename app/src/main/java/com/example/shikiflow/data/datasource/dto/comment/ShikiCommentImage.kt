package com.example.shikiflow.data.datasource.dto.comment

import kotlinx.serialization.Serializable

@Serializable
data class ShikiCommentImage(
    val x16: String,
    val x32: String,
    val x48: String,
    val x64: String,
    val x80: String,
    val x148: String,
    val x160: String,
)