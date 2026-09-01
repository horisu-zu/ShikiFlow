package com.example.shikiflow.domain.model.track.media

import com.example.shikiflow.domain.model.tracks.ShortUserMediaRate

data class MediaTrack(
    val track: MediaUserTrack,
    val shortData: MediaShortData
) {
    companion object {
        fun MediaTrack.toShortUserMediaRate(): ShortUserMediaRate {
            return ShortUserMediaRate(
                id = shortData.id,
                title = shortData.title,
                synonyms = shortData.synonyms ?: emptyList(),
                imageUrl = shortData.poster?.originalUrl ?: "",
                score = track.score,
                status = track.status,
                progress = track.progress,
                genres = shortData.genres,
                tags = shortData.tags
            )
        }
    }
}