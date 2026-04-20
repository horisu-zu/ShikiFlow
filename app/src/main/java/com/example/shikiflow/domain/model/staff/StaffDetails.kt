package com.example.shikiflow.domain.model.staff

import com.example.shikiflow.domain.model.common.PaginatedList
import com.example.shikiflow.domain.model.common.ShortMedia
import com.example.shikiflow.domain.model.media_details.MediaPersonShort
import kotlinx.datetime.LocalDate

data class StaffDetails(
    val id: Int,
    val fullName: String?,
    val nativeName: String?,
    val description: String?,
    val imageUrl: String?,
    val isFavorite: Boolean?,
    val favorites: Int?,
    val birthDate: LocalDate?,
    val shortRoles: Map<String, Int?>,
    val staffCharacterRoles: PaginatedList<MediaPersonShort>,
    val staffAnimeRoles: PaginatedList<ShortMedia>,
    val staffMangaRoles: PaginatedList<ShortMedia>,
    val topicId: Int? = null
)
