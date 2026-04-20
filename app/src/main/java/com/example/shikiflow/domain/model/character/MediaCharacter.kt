package com.example.shikiflow.domain.model.character

import com.example.shikiflow.domain.model.common.PaginatedList
import com.example.shikiflow.domain.model.common.ShortMedia
import com.example.shikiflow.domain.model.media_details.MediaPersonShort

data class MediaCharacter(
    val id: Int,
    val fullName: String,
    val nativeName: String?,
    val alternativeNames: List<String>,
    val imageUrl: String,
    val description: String?,
    val favorites: Int?,
    val isFavorite: Boolean?,
    val voiceActors: List<MediaPersonShort>,
    val animeRoles: PaginatedList<ShortMedia>,
    val mangaRoles: PaginatedList<ShortMedia>,
    val topicId: Int? = null
)
