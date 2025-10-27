package com.example.shikiflow.domain.model.mangadex.chapter_metadata

import com.example.shikiflow.domain.model.mangadex.scanlation_group.ScanlationGroup
import com.example.shikiflow.domain.model.mangadex.scanlation_group.ScanlationGroup.Companion.toDomain
import com.example.shikiflow.domain.model.mangadex.scanlation_group.ScanlationGroupResponse
import com.example.shikiflow.domain.model.mangadex.user.MangaDexUserResponse
import kotlin.time.Instant

data class ChapterMetadata(
    val id: String,
    val title: String?,
    val chapterNumber: String?,
    val translatedLanguage: String,
    val externalUrl: String?,
    val publishAt: Instant,
    val scanlationGroups: List<ScanlationGroup>,
    val uploaderNickname: String?
) {
    companion object {
        fun MangaDexChapterMetadata.toDomain(
            scanlationGroups: List<ScanlationGroupResponse> = emptyList(),
            uploader: MangaDexUserResponse? = null
        ): ChapterMetadata {
            return ChapterMetadata(
                id = this.id,
                title = this.attributes.title,
                chapterNumber = this.attributes.chapter,
                translatedLanguage = this.attributes.translatedLanguage,
                externalUrl = this.attributes.externalUrl,
                publishAt = this.attributes.publishAt,
                scanlationGroups = scanlationGroups.map { it.groupData.toDomain() },
                uploaderNickname = uploader?.data?.attributes?.username
            )
        }
    }
}
