package com.example.shikiflow.data.mapper.shikimori

import com.example.graphql.shikimori.fragment.PersonRoleShort
import com.example.shikiflow.BuildConfig
import com.example.shikiflow.data.datasource.dto.ShikiAnime
import com.example.shikiflow.data.datasource.dto.ShikiManga
import com.example.shikiflow.data.datasource.dto.person.Role
import com.example.shikiflow.data.datasource.dto.person.ShikiPerson
import com.example.shikiflow.data.mapper.common.DateMapper.toLocalDate
import com.example.shikiflow.data.mapper.shikimori.ShikimoriCharacterMapper.toDomain
import com.example.shikiflow.domain.model.common.PaginatedList
import com.example.shikiflow.domain.model.common.StaffMediaRole
import com.example.shikiflow.domain.model.common.VoiceActorMediaRole
import com.example.shikiflow.domain.model.staff.StaffDetails
import com.example.shikiflow.domain.model.staff.StaffShort

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
                        hasNextPage = characterRoles.size > 24,
                        entries = characterRoles
                    )
                } ?: PaginatedList(false, emptyList()),
            staffAnimeRoles = works?.mapNotNull { work ->
                work.anime?.toDomain()
            }
                ?.takeIf { it.isNotEmpty() }
                ?.let { animeRoles ->
                    PaginatedList(
                        hasNextPage = animeRoles.size > 24,
                        entries = animeRoles
                    )
                } ?: PaginatedList(false, emptyList()),
            staffMangaRoles = works?.mapNotNull { work ->
                work.manga?.toDomain()
            }
                ?.takeIf { it.isNotEmpty() }
                ?.let { mangaRoles ->
                    PaginatedList(
                        hasNextPage = mangaRoles.size > 24,
                        entries = mangaRoles
                    )
                } ?: PaginatedList(false, emptyList()),
            topicId = topicId
        )
    }

    fun PersonRoleShort.toDomain(): StaffShort {
        return StaffShort(
            id = person.id.toInt(),
            fullName = person.name,
            imageUrl = person.poster?.originalUrl ?: "",
            roles = rolesEn
        )
    }

    fun ShikiAnime.toStaffRole(role: String): StaffMediaRole {
        return StaffMediaRole(
            shortMedia = this.toDomain(),
            staffRoles = listOf(role)
        )
    }

    fun ShikiManga.toStaffRole(role: String): StaffMediaRole {
        return StaffMediaRole(
            shortMedia = this.toDomain(),
            staffRoles = listOf(role)
        )
    }
    
    fun Role.toVoiceActorRole(): List<VoiceActorMediaRole> {
        val vaRoles = this.characters.map { character ->
            VoiceActorMediaRole(
                characterShort = character.toDomain(),
                shortMediaList = animes.map { it.toDomain() }
            )
        }

        return vaRoles
    }
}