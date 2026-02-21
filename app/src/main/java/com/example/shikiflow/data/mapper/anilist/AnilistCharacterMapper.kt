package com.example.shikiflow.data.mapper.anilist

import com.example.graphql.anilist.CharacterDetailsQuery
import com.example.graphql.anilist.CharactersQuery
import com.example.graphql.anilist.fragment.ALCharacterShort
import com.example.graphql.anilist.fragment.ALMediaBrowseShort
import com.example.shikiflow.data.mapper.anilist.AnilistStaffMapper.toDomain
import com.example.shikiflow.data.mapper.common.MediaTypeMapper.toDomain
import com.example.shikiflow.data.mapper.common.RateStatusMapper.toDomain
import com.example.graphql.anilist.type.CharacterRole as AnilistCharacterRole
import com.example.shikiflow.domain.model.character.CharacterRole
import com.example.shikiflow.domain.model.character.MediaCharacter
import com.example.shikiflow.domain.model.character.MediaCharacterShort
import com.example.shikiflow.domain.model.character.MediaRole
import com.example.shikiflow.domain.model.common.PaginatedList
import com.example.shikiflow.domain.model.media_details.MediaPersonShort
import com.example.shikiflow.domain.model.tracks.MediaType

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

    fun ALMediaBrowseShort.toDomain(): MediaRole {
        return MediaRole(
            id = id,
            mediaType = type?.toDomain() ?: MediaType.ANIME,
            title = title?.romaji ?: "",
            coverImageUrl = coverImage?.large ?: "",
            userRateStatus = mediaListEntry?.status?.toDomain()
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
            voiceActors = emptyList(),
            animeRoles = PaginatedList(
                hasNextPage = anime?.pageInfo?.hasNextPage == true,
                entries = anime?.edges?.mapNotNull { it?.node?.aLMediaBrowseShort?.toDomain() }.orEmpty()
            ),
            mangaRoles = PaginatedList(
                hasNextPage = manga?.pageInfo?.hasNextPage == true,
                entries = manga?.edges?.mapNotNull { it?.node?.aLMediaBrowseShort?.toDomain() }.orEmpty()
            ),
            topicId = null
        )
    }
}