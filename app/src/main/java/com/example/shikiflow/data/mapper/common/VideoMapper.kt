package com.example.shikiflow.data.mapper.common

import com.example.graphql.anilist.fragment.ALVideoShort
import com.example.shikiflow.domain.model.media_details.VideoShort as DomainVideo
import com.example.graphql.shikimori.fragment.VideoShort

object VideoMapper {
    private const val YOUTUBE_VIDEO_URL = "https://www.youtube.com/watch?v="
    private const val DAILY_MOTION_VIDEO_URL = "https://www.dailymotion.com/video/"

    fun VideoShort.toDomainVideo(): DomainVideo {
        return DomainVideo(
            title = name,
            url = url,
            thumbnailUrl = if (!imageUrl.startsWith("https:")) "https:$imageUrl"
                else imageUrl
        )
    }

    fun ALVideoShort.toDomainVideo(): DomainVideo {
        return DomainVideo(
            title = null,
            url = buildString {
                when (site) {
                    "youtube" -> append(YOUTUBE_VIDEO_URL)
                    "dailymotion" -> append(DAILY_MOTION_VIDEO_URL)
                }
                append(id)
            },
            thumbnailUrl = thumbnail ?: ""
        )
    }
}