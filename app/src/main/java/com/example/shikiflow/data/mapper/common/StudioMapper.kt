package com.example.shikiflow.data.mapper.common

import com.example.graphql.anilist.fragment.ALStudio
import com.example.graphql.anilist.fragment.ALStudioShort
import com.example.graphql.shikimori.AnimeDetailsQuery
import com.example.shikiflow.domain.model.media_details.StudioShort
import com.example.shikiflow.domain.model.studio.Studio

object StudioMapper {
    fun ALStudioShort.toStudioShort(): StudioShort {
        return StudioShort(
            id = id,
            name = name
        )
    }

    fun ALStudio.toStudio(): Studio {
        return Studio(
            id = id,
            name = name,
            isFavorite = isFavourite,
            favorites = favourites
        )
    }

    fun AnimeDetailsQuery.Studio.toStudioShort(): StudioShort {
        return StudioShort(
            id = id.toInt(),
            name = name,
            imageUrl = imageUrl
        )
    }
}