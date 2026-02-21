package com.example.shikiflow.domain.model.character

import com.example.shikiflow.domain.model.common.PaginatedList
import com.example.shikiflow.domain.model.media_details.MediaPersonShort

data class MediaCharacter(
    val id: Int,
    val fullName: String,
    val nativeName: String?,
    val alternativeNames: List<String>,
    val imageUrl: String,
    val description: String?,
    val voiceActors: List<MediaPersonShort>,
    val animeRoles: PaginatedList<MediaRole>,
    val mangaRoles: PaginatedList<MediaRole>,
    val topicId: Int? = null
)
