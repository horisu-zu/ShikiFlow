package com.example.shikiflow.domain.model.common

data class FileSize(
    val value: Double,
    val unit: SizeUnit
) {
    enum class SizeUnit { B, KB, MB, GB }
}
