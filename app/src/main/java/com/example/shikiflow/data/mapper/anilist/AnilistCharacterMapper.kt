package com.example.shikiflow.data.mapper.anilist

import com.example.graphql.anilist.CharacterDetailsQuery
import com.example.graphql.anilist.CharactersQuery
import com.example.graphql.anilist.fragment.ALCharacterMediaRoles
import com.example.graphql.anilist.fragment.ALCharacterShort
import com.example.graphql.anilist.fragment.ALMediaBrowseShort
import com.example.shikiflow.data.mapper.anilist.AnilistStaffMapper.toDomain
import com.example.graphql.anilist.type.CharacterRole as AnilistCharacterRole
import com.example.shikiflow.domain.model.character.CharacterRole
import com.example.shikiflow.domain.model.character.MediaCharacter
import com.example.shikiflow.domain.model.character.MediaCharacterShort
import com.example.shikiflow.domain.model.common.CharacterMediaRole
import com.example.shikiflow.domain.model.common.PaginatedList
import com.example.shikiflow.domain.model.media_details.MediaPersonShort

object AnilistCharacterMapper {
    fun AnilistCharacterRole.toDomain(): CharacterRole {
        return when(this) {
            AnilistCharacterRole.MAIN -> CharacterRole.MAIN
            AnilistCharacterRole.SUPPORTING -> CharacterRole.SUPPORTING
            AnilistCharacterRole.BACKGROUND -> CharacterRole.BACKGROUND
            AnilistCharacterRole.UNKNOWN__ -> CharacterRole.UNKNOWN
        }
    }

    fun ALCharacterShort.toDomain(): MediaPersonShort {
        return MediaPersonShort(
            id = id,
            fullName = name?.full ?: "",
            imageUrl = image?.large ?: ""
        )
    }

    fun CharactersQuery.Edge.toDomain(): MediaCharacterShort {
        return MediaCharacterShort(
            mediaCharacter = node!!.aLCharacterShort.toDomain(),
            role = role?.toDomain() ?: CharacterRole.UNKNOWN,
            mediaPerson = voiceActors?.firstNotNullOfOrNull { it?.aLStaffShort?.toDomain() }
        )
    }

    fun ALMediaBrowseShort.toDomain(characterRole: CharacterRole): CharacterMediaRole {
        return CharacterMediaRole(
            shortMedia = this.toDomain(),
            characterRole = characterRole
        )
    }

    fun CharacterDetailsQuery.Character.toDomain(): MediaCharacter {
        return MediaCharacter(
            id = id,
            fullName = name?.full ?: "",
            nativeName = name?.native,
            alternativeNames = name?.alternativeSpoiler?.mapNotNull { it }.orEmpty(),
            imageUrl = image?.large ?: "",
            description = description,
            isFavorite = isFavourite,
            favorites = favourites,
            voiceActors = emptyList(),
            animeRoles = PaginatedList(
                hasNextPage = anime?.aLCharacterMediaRoles?.pageInfo?.hasNextPage == true,
                entries = anime?.aLCharacterMediaRoles?.edges?.mapNotNull {
                    it?.node?.aLMediaBrowseShort?.toDomain()
                }.orEmpty(),
            ),
            mangaRoles = PaginatedList(
                hasNextPage = manga?.aLCharacterMediaRoles?.pageInfo?.hasNextPage == true,
                entries = manga?.aLCharacterMediaRoles?.edges?.mapNotNull {
                    it?.node?.aLMediaBrowseShort?.toDomain()
                }.orEmpty()
            ),
            topicId = null
        )
    }

    fun ALCharacterMediaRoles.toCharacterMediaRole(): List<CharacterMediaRole> {
        return this.edges?.mapNotNull {
            it?.node?.aLMediaBrowseShort?.toDomain(
                characterRole = it.characterRole?.toDomain() ?: CharacterRole.UNKNOWN
            )
        }.orEmpty()
    }
}