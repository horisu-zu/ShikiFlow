package com.example.shikiflow.data.mapper.shikimori

import com.example.graphql.shikimori.fragment.PersonRoleShort
import com.example.graphql.shikimori.fragment.ShikiStaffShort
import com.example.shikiflow.BuildConfig
import com.example.shikiflow.data.datasource.dto.ShikiAnime
import com.example.shikiflow.data.datasource.dto.ShikiManga
import com.example.shikiflow.data.datasource.dto.person.Role
import com.example.shikiflow.data.datasource.dto.person.ShikiPerson
import com.example.shikiflow.data.mapper.common.DateMapper.toLocalDate
import com.example.shikiflow.data.mapper.common.StaffNameMapper.toStaffName
import com.example.shikiflow.data.mapper.common.StaffRoleMapper.toStaffRole
import com.example.shikiflow.data.mapper.shikimori.ShikimoriCharacterMapper.toDomain
import com.example.shikiflow.domain.model.common.PaginatedList
import com.example.shikiflow.domain.model.common.StaffMediaRole
import com.example.shikiflow.domain.model.common.VoiceActorMediaRole
import com.example.shikiflow.domain.model.media_details.MediaPersonShort
import com.example.shikiflow.domain.model.staff.StaffDetails
import com.example.shikiflow.domain.model.staff.StaffKind
import com.example.shikiflow.domain.model.staff.StaffRole
import com.example.shikiflow.domain.model.staff.StaffShort

object ShikimoriStaffMapper {
    fun ShikiPerson.toStaffDetails(imageUrl: String?): StaffDetails {
        val staffKind = when {
            mangaka == true && seyu == true -> {
                val seyuRoles = groupedRoles?.firstOrNull { it.role == "Сэйю"}?.count ?: 0
                val mangakaRoles = groupedRoles?.filter { staff ->
                    staff.role in staffMangakaRolesRu
                }?.maxOf { it.count } ?: 0

                if (seyuRoles > mangakaRoles) {
                    StaffKind.SEYU
                } else StaffKind.MANGAKA
            }
            producer == true && seyu == true -> {
                val seyuRoles = groupedRoles?.firstOrNull { it.role == "Сэйю"}?.count ?: 0
                val producerRoles = groupedRoles?.filter { role ->
                    role.role in staffRoleRu
                }?.maxOf { it.count } ?: 0

                if (seyuRoles > producerRoles) {
                    StaffKind.SEYU
                } else StaffKind.PRODUCER
            }
            producer == true && mangaka == true -> {
                val producerRoles = groupedRoles?.filter { role ->
                    role.role in staffRoleRu
                }?.maxOf { it.count } ?: 0
                val mangakaRoles = groupedRoles?.filter { staff ->
                    staff.role in staffMangakaRolesRu
                }?.maxOf { it.count } ?: 0

                if (mangakaRoles > producerRoles) {
                    StaffKind.MANGAKA
                } else StaffKind.PRODUCER
            }
            producer == true -> StaffKind.PRODUCER
            mangaka == true -> StaffKind.MANGAKA
            seyu == true -> StaffKind.SEYU
            else -> StaffKind.OTHER
        }

        return StaffDetails(
            id = id,
            fullName = name.toStaffName(russian, japanese),
            description = null,
            attributes = null,
            imageUrl = imageUrl ?: "${BuildConfig.SHIKI_BASE_URL}${image.original}",
            staffKind = staffKind,
            isFavorite = when(staffKind) {
                StaffKind.PRODUCER -> producerFavored
                StaffKind.MANGAKA -> mangakaFavored
                StaffKind.SEYU -> seyuFavored
                else -> personFavored
            },
            favorites = null,
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
                work.anime?.toStaffRole(work.role)
            }
                ?.takeIf { it.isNotEmpty() }
                ?.let { animeRoles ->
                    PaginatedList(
                        hasNextPage = animeRoles.size > 24,
                        entries = animeRoles
                    )
                } ?: PaginatedList(false, emptyList()),
            staffMangaRoles = works?.mapNotNull { work ->
                work.manga?.toStaffRole(work.role)
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
            fullName = person.name.toStaffName(person.russian, null),
            imageUrl = person.poster?.originalUrl ?: "",
            roles = rolesEn.mapIndexed { index, roleEn ->
                roleEn.toStaffRole(russian = rolesRu.getOrNull(index))
            }
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
        val vaRoles = characters.map { character ->
            VoiceActorMediaRole(
                characterShort = character.toDomain(),
                shortMediaList = animes.map { it.toDomain() }
            )
        }

        return vaRoles
    }

    fun ShikiStaffShort.toStaff(): MediaPersonShort {
        return MediaPersonShort(
            id = id.toInt(),
            fullName = name.toStaffName(russian, null),
            imageUrl = poster?.posterShort?.originalUrl ?: ""
        )
    }

    fun StaffKind.toShikiKind(): String {
        return when (this) {
            StaffKind.SEYU -> "seyu"
            StaffKind.MANGAKA -> "mangaka"
            StaffKind.PRODUCER -> "producer"
            StaffKind.OTHER -> "person"
        }
    }

    fun List<StaffShort>.sortByRole(): List<StaffShort> {
        return sortedBy { staff ->
            staff.roles.rolePriority()
        }
    }

    private fun List<StaffRole>.rolePriority(): Int {
        return minOfOrNull { role ->
            staffRoleOrder.indexOf(role.english).takeIf { it >= 0 } ?: Int.MAX_VALUE
        } ?: Int.MAX_VALUE
    }

    private val staffRoleOrder = listOf(
        "Original Creator",
        "Director",
        "Chief Producer",
        "Theme Song Performance",
        "Theme Song Arrangement",
        "Art Director",
        "Assistant Producer",
        "Series Composition",
        "Chief Animation Director",
        "Character Design",
        "Color Design",
        "Executive Producer",
        "Episode Director",
        "Storyboard",
        "Music",
        "Sound Director",
        "Director of Photography",
        "Editing",
        "Producer",
        "Planning",
        "Assistant Director",
        "Theme Song Composition",
        "Theme Song Lyrics",
        "Script",
        "Animation Director",
        "Assistant Animation Director",
        "Background Art",
        "Recording",
        "Recording Assistant",
        "Sound Effects",
        "ADR Director"
    )

    private val staffMangakaRolesRu = listOf(
        "Автор оригинала",
        "Сюжет и иллюстрации",
        "Рисовка"
    )

    private val staffRoleRu = listOf(
        "Режиссёр",
        "Главный продюсер",
        "Исполнение гл. муз. темы",
        "Аранжировка гл. муз. темы",
        "Арт-директор",
        "Ассистент продюсера",
        "Компоновка серий",
        "Главный аниматор",
        "Дизайн персонажей",
        "Дизайн цвета",
        "Исполнительн. продюсер",
        "Режиссёр эпизодов",
        "Раскадровка",
        "Музыка",
        "Звукорежиссёр",
        "Оператор-постановщик",
        "Монтаж",
        "Продюсер",
        "Планирование",
        "Помощник режиссёра",
        "Композитор гл. муз. темы",
        "Лирика гл. муз. темы",
        "Сценарий",
        "Режиссёр анимации",
        "Помощник режиссёра анимации",
        "Фоновая рисовка",
        "Звукооператор",
        "Помощник звукооператора",
        "Звуковые эффекты",
        "Режиссёр озвучки"
    )
}