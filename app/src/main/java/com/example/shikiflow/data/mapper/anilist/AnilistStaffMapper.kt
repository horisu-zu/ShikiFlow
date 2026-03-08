package com.example.shikiflow.data.mapper.anilist

import com.example.graphql.anilist.StaffDetailsQuery
import com.example.graphql.anilist.fragment.ALMediaBrowseShort
import com.example.graphql.anilist.fragment.ALMediaStaffRoles
import com.example.graphql.anilist.fragment.ALStaffShort
import com.example.shikiflow.data.mapper.anilist.AnilistCharacterMapper.toDomain
import com.example.shikiflow.data.mapper.common.DateMapper.toLocalDate
import com.example.shikiflow.data.mapper.common.MediaTypeMapper.toDomain
import com.example.shikiflow.data.mapper.common.RateStatusMapper.toDomain
import com.example.shikiflow.domain.model.common.PaginatedList
import com.example.shikiflow.domain.model.common.ShortMedia
import com.example.shikiflow.domain.model.common.StaffMediaRole
import com.example.shikiflow.domain.model.media_details.MediaPersonShort
import com.example.shikiflow.domain.model.staff.StaffDetails
import com.example.shikiflow.domain.model.tracks.MediaType

object AnilistStaffMapper {
    fun ALStaffShort.toDomain(): MediaPersonShort {
        return MediaPersonShort(
            id = id,
            fullName = name?.full ?: "",
            imageUrl = image?.large ?: ""
        )
    }

    fun ALMediaBrowseShort.toDomain(): ShortMedia {
        return ShortMedia(
            id = id,
            title = title?.romaji ?: "",
            mediaType = type?.toDomain() ?: MediaType.ANIME,
            coverImageUrl = coverImage?.large ?: "",
            userRateStatus = mediaListEntry?.status?.toDomain()
        )
    }

    fun ALMediaBrowseShort.toDomain(staffRoles: List<String>): StaffMediaRole {
        return StaffMediaRole(
            shortMedia = this.toDomain(),
            staffRoles = staffRoles
        )
    }

    fun ALMediaStaffRoles?.toDomain(): PaginatedList<ShortMedia> {
        val hasNextPage = this?.pageInfo?.hasNextPage == true
        val mediaRoles = this?.edges
            ?.filterNotNull()
            ?.distinctBy { it.node?.aLMediaBrowseShort?.id }
            ?.mapNotNull { edge -> edge.node?.aLMediaBrowseShort?.toDomain() }

        return mediaRoles
            ?.takeIf { it.isNotEmpty() }
            ?.let { entries ->
                PaginatedList(
                    hasNextPage = hasNextPage,
                    entries = entries
                )
            } ?: PaginatedList(false, emptyList())
    }

    fun ALMediaStaffRoles?.toStaffMediaRoles(): List<StaffMediaRole> {
        return this?.edges
            ?.filterNotNull()
            ?.groupBy { it.node?.aLMediaBrowseShort?.id }
            ?.mapNotNull { (_, edges) ->
                val staffRoles = edges.mapNotNull { it.staffRole }
                edges.first().node?.aLMediaBrowseShort?.toDomain(staffRoles)
            } ?: emptyList()
    }

    fun StaffDetailsQuery.Staff.toDomain(): StaffDetails {
        return StaffDetails(
            id = id,
            fullName = name?.full,
            nativeName = name?.native,
            description = description,
            imageUrl = image?.large,
            birthDate = dateOfBirth?.date?.toLocalDate(),
            shortRoles = primaryOccupations?.filterNotNull()
                ?.associateWith { null }
                ?: emptyMap(),
            staffCharacterRoles = characters?.nodes?.mapNotNull { node ->
                node?.aLCharacterShort?.toDomain()
            }
                ?.takeIf { it.isNotEmpty() }
                ?.let { characterRoles ->
                    PaginatedList(
                        hasNextPage = characters.pageInfo?.hasNextPage == true,
                        entries = characterRoles
                    )
                } ?: PaginatedList(hasNextPage = false, entries = emptyList()),
            staffAnimeRoles = animeStaffRoles?.aLMediaStaffRoles.toDomain(),
            staffMangaRoles = mangaStaffRoles?.aLMediaStaffRoles.toDomain(),
            topicId = null
        )
    }
}