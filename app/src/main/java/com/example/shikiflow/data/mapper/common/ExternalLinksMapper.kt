package com.example.shikiflow.data.mapper.common

import com.example.graphql.anilist.MediaExternalLinksQuery
import com.example.shikiflow.data.datasource.dto.ExternalLink
import com.example.shikiflow.domain.model.media_details.ExternalLinkData

object ExternalLinksMapper {
    fun ExternalLink.toDomain(): ExternalLinkData {
        return ExternalLinkData(
            icon = kind,
            siteName = source,
            url = url
        )
    }

    fun MediaExternalLinksQuery.ExternalLink.toDomain(): ExternalLinkData {
        return ExternalLinkData(
            icon = icon,
            siteName = site,
            url = url ?: ""
        )
    }
}