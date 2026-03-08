package com.example.shikiflow.data.mapper.shikimori

import com.example.graphql.shikimori.fragment.CharacterShort
import com.example.graphql.shikimori.fragment.ShikiCharacterRole
import com.example.shikiflow.BuildConfig
import com.example.shikiflow.data.datasource.dto.ShikiAnime
import com.example.shikiflow.data.datasource.dto.ShikiCharacter
import com.example.shikiflow.data.datasource.dto.ShikiManga
import com.example.shikiflow.data.datasource.dto.ShikiSeyu
import com.example.shikiflow.data.datasource.dto.person.Character
import com.example.shikiflow.domain.model.character.MediaCharacter
import com.example.shikiflow.domain.model.character.CharacterRole
import com.example.shikiflow.domain.model.character.MediaCharacterShort
import com.example.shikiflow.domain.model.common.CharacterMediaRole
import com.example.shikiflow.domain.model.common.PaginatedList
import com.example.shikiflow.domain.model.common.ShortMedia
import com.example.shikiflow.domain.model.media_details.MediaPersonShort
import com.example.shikiflow.domain.model.tracks.MediaType

object ShikimoriCharacterMapper {
    fun String.toCharacterRole(): CharacterRole {
        return when(this) {
            "Main" -> CharacterRole.MAIN
            "Supporting" -> CharacterRole.SUPPORTING
            else -> CharacterRole.UNKNOWN
        }
    }

    fun CharacterShort.toDomain(): MediaPersonShort {
        return MediaPersonShort(
            id = id.toInt(),
            fullName = name,
            imageUrl = poster?.posterShort?.mainUrl ?: ""
        )
    }

    fun ShikiCharacterRole.toDomain(): MediaCharacterShort {
        return MediaCharacterShort(
            mediaCharacter = character.characterShort.toDomain(),
            role = rolesEn.map { it.toCharacterRole() }.first(),
            mediaPerson = null
        )
    }

    fun Character.toDomain(): MediaPersonShort {
        return MediaPersonShort(
            id = id,
            fullName = name,
            imageUrl = BuildConfig.SHIKI_BASE_URL + image.original
        )
    }

    fun ShikiAnime.toDomain(): ShortMedia {
        return ShortMedia(
            id = this.id ?: 0,
            mediaType = MediaType.ANIME,
            title = this.name ?: "",
            coverImageUrl = BuildConfig.SHIKI_BASE_URL + this.image?.original,
            userRateStatus = null
        )
    }

    fun ShikiManga.toDomain(): ShortMedia {
        return ShortMedia(
            id = this.id ?: 0,
            mediaType = MediaType.ANIME,
            title = this.name ?: "",
            coverImageUrl = BuildConfig.SHIKI_BASE_URL + this.image?.original,
            userRateStatus = null
        )
    }

    fun ShikiSeyu.toDomain(): MediaPersonShort {
        return MediaPersonShort(
            id = this.id,
            fullName = this.name,
            imageUrl = BuildConfig.SHIKI_BASE_URL + this.image?.original
        )
    }

    fun ShikiCharacter.toDomain(): MediaCharacter {
        return MediaCharacter(
            id = this.id,
            fullName = this.name,
            nativeName = this.japanese,
            alternativeNames = listOfNotNull(this.altName),
            imageUrl = BuildConfig.SHIKI_BASE_URL + this.image.original,
            description = this.descriptionHtml,
            voiceActors = this.seyu?.map { it.toDomain() } ?: emptyList(),
            animeRoles = PaginatedList(
                hasNextPage = (this.animes?.size ?: 0) > 24,
                entries = this.animes?.map { it.toDomain() }.orEmpty()
            ),
            mangaRoles = PaginatedList(
                hasNextPage = (this.mangas?.size ?: 0) > 24,
                entries = this.mangas?.map { it.toDomain() }.orEmpty()
            ),
            topicId = this.topicId
        )
    }

    fun ShortMedia.toCharacterRole(): CharacterMediaRole {
        return CharacterMediaRole(shortMedia = this)
    }
}