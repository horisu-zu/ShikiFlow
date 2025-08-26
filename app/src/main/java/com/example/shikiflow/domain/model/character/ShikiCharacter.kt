package com.example.shikiflow.domain.model.character

import com.example.shikiflow.domain.model.anime.ShikiAnime
import com.example.shikiflow.domain.model.anime.ShikiManga
import com.example.shikiflow.domain.model.common.ShikiImage
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ShikiCharacter(
    val id: Int?,
    val name: String?,
    val russian: String?,
    val image: ShikiImage,
    @SerialName("altname") val altName: String?,
    val japanese: String?,
    val description: String?,
    @SerialName("description_html") val descriptionHtml: String?,
    @SerialName("description_source") val descriptionSource: String?,
    val favoured: Boolean?,
    @SerialName("thread_id") val threadId: Int?,
    @SerialName("topic_id") val topicId: Int?,
    @SerialName("updated_at") val updatedAt: String?,
    val seyu: List<ShikiSeyu>?,
    val animes: List<ShikiAnime>?,
    val mangas: List<ShikiManga>?
)
