package com.example.shikiflow.data.mapper.shikimori

import com.example.shikiflow.BuildConfig
import com.example.shikiflow.data.datasource.dto.person.ShikiPerson
import com.example.shikiflow.data.mapper.common.DateMapper.toLocalDate
import com.example.shikiflow.data.mapper.shikimori.ShikimoriCharacterMapper.toDomain
import com.example.shikiflow.domain.model.common.PaginatedList
import com.example.shikiflow.domain.model.staff.StaffDetails

object ShikimoriStaffMapper {
    fun ShikiPerson.toDomain(): StaffDetails {
        return StaffDetails(
            id = id,
            fullName = name,
            nativeName = japanese,
            description = null,
            imageUrl = "${BuildConfig.SHIKI_BASE_URL}${image.original}",
            birthDate = birthDate?.toLocalDate(),
            shortRoles = groupedRoles?.associateBy { it.role }
                ?.mapValues { it.value.count } ?: emptyMap(),
            staffCharacterRoles = roles?.flatMap { role ->
                role.characters.map { it.toDomain() }
            }
                ?.takeIf { it.isNotEmpty() }
                ?.let { characterRoles ->
                    PaginatedList(
                        hasNextPage = false,
                        entries = characterRoles
                    )
                },
            staffAnimeRoles = if(seyu != true) {
                works?.mapNotNull { work ->
                    work.anime?.toDomain()
                }
                    ?.takeIf { it.isNotEmpty() }
                    ?.let { animeRoles ->
                        PaginatedList(
                            hasNextPage = false,
                            entries = animeRoles
                        )
                    }
            } else null,
            staffMangaRoles = if(seyu != true) {
                works?.mapNotNull { work ->
                    work.manga?.toDomain()
                }
                    ?.takeIf { it.isNotEmpty() }
                    ?.let { mangaRoles ->
                        PaginatedList(
                            hasNextPage = false,
                            entries = mangaRoles
                        )
                    }
            } else null,
            topicId = topicId
        )
    }
}