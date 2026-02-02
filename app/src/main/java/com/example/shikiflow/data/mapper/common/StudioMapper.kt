package com.example.shikiflow.data.mapper.common

import com.example.graphql.anilist.fragment.ALStudioShort
import com.example.graphql.shikimori.AnimeDetailsQuery
import com.example.graphql.shikimori.fragment.StudioShort
import com.example.shikiflow.domain.model.media_details.Studio

object StudioMapper {
    fun ALStudioShort.toDomain(): Studio {
        return Studio(
            id = id,
            name = name
        )
    }

    fun AnimeDetailsQuery.Studio.toDomain(): Studio {
        return Studio(
            id = id.toInt(),
            name = name,
            imageUrl = imageUrl
        )
    }
}