package com.example.shikiflow.domain.model.character

import com.example.shikiflow.domain.model.common.PaginatedList
import com.example.shikiflow.domain.model.common.ShortMedia
import com.example.shikiflow.domain.model.media_details.MediaPersonShort
import com.example.shikiflow.domain.model.staff.StaffAttributes
import com.example.shikiflow.domain.model.staff.StaffName

data class MediaCharacter(
    val id: Int,
    val fullName: StaffName,
    val alternativeNames: List<String>,
    val imageUrl: String,
    val description: String?,
    val attributes: StaffAttributes?,
    val favorites: Int?,
    val isFavorite: Boolean?,
    val voiceActors: List<MediaPersonShort>,
    val animeRoles: PaginatedList<ShortMedia>,
    val mangaRoles: PaginatedList<ShortMedia>,
    val topicId: Int? = null
)
