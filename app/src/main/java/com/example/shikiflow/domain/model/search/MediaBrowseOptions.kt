package com.example.shikiflow.domain.model.search

import com.example.shikiflow.domain.model.media_details.AgeRating
import com.example.shikiflow.domain.model.media_details.CountryOfOrigin
import com.example.shikiflow.domain.model.media_details.Genre
import com.example.shikiflow.domain.model.media_details.MediaSeason
import com.example.shikiflow.domain.model.media_details.MediaStatus
import com.example.shikiflow.domain.model.media_details.MediaTagEnum
import com.example.shikiflow.domain.model.sort.MediaSort
import com.example.shikiflow.domain.model.sort.Sort
import com.example.shikiflow.domain.model.track.MediaFormat
import com.example.shikiflow.domain.model.tracks.MediaType

data class MediaBrowseOptions(
    val mediaType: MediaType,
    val name: String? = null,
    val status: MediaStatus? = null,
    val sort: Sort<MediaSort>? = null,
    val format: MediaFormat? = null,
    val season: MediaSeason? = null,
    val genres: List<Genre> = emptyList(),
    val tags: List<MediaTagEnum> = emptyList(),
    val score: Int? = null,
    val ageRating: AgeRating? = null,
    val countryOfOrigin: CountryOfOrigin? = null
) {
    companion object {
        fun MediaBrowseOptions.isEmpty(): Boolean {
            return name == null &&
                status == null &&
                sort == null &&
                format == null &&
                season == null &&
                genres.isEmpty() &&
                tags.isEmpty() &&
                score == null &&
                ageRating == null &&
                countryOfOrigin == null
        }
    }
}