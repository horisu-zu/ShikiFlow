package com.example.shikiflow.data.mapper

import com.example.shikiflow.BuildConfig
import com.example.shikiflow.data.datasource.dto.mangadex.aggregate.AggregateChapterResponse
import com.example.shikiflow.data.datasource.dto.mangadex.aggregate.AggregateResponse
import com.example.shikiflow.data.datasource.dto.mangadex.aggregate.AggregateVolumeResponse
import com.example.shikiflow.data.datasource.dto.mangadex.cover.CoverResponse
import com.example.shikiflow.data.datasource.dto.mangadex.manga.Attributes
import com.example.shikiflow.data.datasource.dto.mangadex.manga.Data
import com.example.shikiflow.data.datasource.dto.mangadex.manga.Relationship
import com.example.shikiflow.domain.model.mangadex.aggregate.AggregatedChapter
import com.example.shikiflow.domain.model.mangadex.aggregate.AggregatedManga
import com.example.shikiflow.domain.model.mangadex.aggregate.AggregatedVolume
import com.example.shikiflow.data.datasource.dto.mangadex.chapter.ChapterResponse
import com.example.shikiflow.data.datasource.dto.mangadex.chapter_metadata.ChapterMetadataAttributes
import com.example.shikiflow.data.datasource.dto.mangadex.chapter_metadata.MangaDexChapterMetadata
import com.example.shikiflow.data.datasource.dto.mangadex.scanlation_group.ScanlationGroupResponse
import com.example.shikiflow.data.datasource.dto.mangadex.user.MangaDexUserResponse
import com.example.shikiflow.domain.model.mangadex.chapter.MangaChapter
import com.example.shikiflow.domain.model.mangadex.chapter_metadata.ChapterMetadata
import com.example.shikiflow.domain.model.mangadex.chapter_metadata.MangaChapterMetadata
import com.example.shikiflow.domain.model.mangadex.chapter_metadata.MetadataAttributes
import com.example.shikiflow.domain.model.mangadex.cover.MangaCover
import com.example.shikiflow.domain.model.mangadex.manga.Manga
import com.example.shikiflow.domain.model.mangadex.manga.MangaAttributes
import com.example.shikiflow.domain.model.mangadex.manga.MangaRelationship
import com.example.shikiflow.domain.model.mangadex.scanlation_group.ScanlationGroup
import com.example.shikiflow.domain.model.mangadex.user.MangaDexUser

object MangaDexMapper {
    fun AggregateResponse.toDomain(): AggregatedManga {
        return AggregatedManga(
            volumes = this.volumes.values.map { it.toDomain() }
        )
    }

    fun AggregateVolumeResponse.toDomain(): AggregatedVolume {
        return AggregatedVolume(
            volume = this.volume,
            count = this.count,
            chapters = this.chapters.values.map { it.toDomain() }
        )
    }

    fun AggregateChapterResponse.toDomain(): AggregatedChapter {
        return AggregatedChapter(
            chapter = this.chapter,
            id = this.id,
            others = this.others,
            count = this.count
        )
    }

    fun Data.toDomain(): Manga {
        return Manga(
            id = this.id,
            attributes = this.attributes.toDomain(),
            relationships = this.relationships.map { it.toDomain() },
            type = this.type
        )
    }

    fun Attributes.toDomain(): MangaAttributes {
        return MangaAttributes(
            title = title.jaRo ?: title.en ?: "",
            status = status,
            malId = links?.mal
        )
    }

    fun Relationship.toDomain(): MangaRelationship {
        return MangaRelationship(
            id = id,
            type = type
        )
    }

    fun CoverResponse.toDomain(): MangaCover {
        return MangaCover(
            id = data.id,
            type = data.type,
            coverUrl = "${BuildConfig.MANGADEX_UPLOADS_URL}/covers/" +
                    "${data.relationships.find { it.type == "manga" }?.id}/${data.attributes.fileName}"
        )
    }

    fun ChapterResponse.toDomain(): MangaChapter {
        return MangaChapter(
            baseUrl = baseUrl,
            hash = chapter.hash,
            data = chapter.data,
            dataSaver = chapter.dataSaver
        )
    }

    fun MangaDexUserResponse.toDomain(): MangaDexUser {
        return MangaDexUser(
            id = data.id,
            username = data.attributes.username,
            roles = data.attributes.roles
        )
    }

    fun ScanlationGroupResponse.toDomain(): ScanlationGroup {
        return ScanlationGroup(
            id = groupDataResponse.id,
            name = groupDataResponse.attributes.name,
            isOfficial = groupDataResponse.attributes.official,
            website = groupDataResponse.attributes.website
        )
    }

    fun MangaDexChapterMetadata.toDomain(): MangaChapterMetadata {
        return MangaChapterMetadata(
            id = id,
            type = type,
            attributes = attributes.toDomain(),
            relationships = relationships.map { it.toDomain() }
        )
    }

    fun ChapterMetadataAttributes.toDomain(): MetadataAttributes {
        return MetadataAttributes(
            title = title,
            volume = volume,
            chapter = chapter,
            pages = pages,
            translatedLanguage = translatedLanguage,
            uploader = uploader,
            externalUrl = externalUrl,
            version = version,
            createdAt = createdAt,
            updatedAt = updatedAt,
            publishAt = publishAt,
            readableAt = readableAt,
            isUnavailable = isUnavailable
        )
    }
}