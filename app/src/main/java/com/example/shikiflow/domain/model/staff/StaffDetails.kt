package com.example.shikiflow.domain.model.staff

import com.example.shikiflow.domain.model.common.PaginatedList
import com.example.shikiflow.domain.model.common.StaffMediaRole
import com.example.shikiflow.domain.model.media_details.MediaPersonShort
import kotlinx.datetime.LocalDate

data class StaffDetails(
    val id: Int,
    val fullName: StaffName,
    val description: String?,
    val attributes: StaffAttributes?,
    val imageUrl: String?,
    val isFavorite: Boolean?,
    val favorites: Int?,
    val birthDate: LocalDate?,
    val shortRoles: Map<String, Int?>,
    val staffCharacterRoles: PaginatedList<MediaPersonShort>,
    val staffAnimeRoles: PaginatedList<StaffMediaRole>,
    val staffMangaRoles: PaginatedList<StaffMediaRole>,
    val topicId: Int? = null
)
