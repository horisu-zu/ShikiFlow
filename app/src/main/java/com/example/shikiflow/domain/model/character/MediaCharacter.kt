package com.example.shikiflow.domain.model.character

import com.example.shikiflow.domain.model.media_details.MediaPersonShort

data class MediaCharacter(
    val id: Int,
    val fullName: String,
    val nativeName: String?,
    val alternativeNames: List<String>,
    val imageUrl: String,
    val description: String?,
    val voiceActors: List<MediaPersonShort>,
    val animeRoles: List<MediaRole>,
    val mangaRoles: List<MediaRole>,
    val topicId: Int? = null
)
