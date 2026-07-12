package com.example.shikiflow.presentation.common.mappers

import com.example.shikiflow.domain.model.common.ScoreFormat
import com.example.shikiflow.domain.model.media_details.MediaTitle
import com.example.shikiflow.domain.model.user.MediaComparison
import com.example.shikiflow.presentation.common.mappers.ScoreFormatMapper.formatValue
import com.example.shikiflow.presentation.viewmodel.user.compare.CompareMediaFilters

object MediaComparisonMapper {
    fun List<MediaComparison>.filterEntries(
        filters: CompareMediaFilters,
        scoreFormat: ScoreFormat
    ): List<MediaComparison> {
        if (filters.query.isBlank() && filters.selectedGenres.isEmpty() &&
            filters.selectedTags.isEmpty() &&
            filters.currentUserScoreRange == null &&
            filters.targetUserScoreRange == null
        ) {
            return this
        }

        return filter { item ->
            val whereTitle = filters.query.isBlank() || item.matchesQuery(filters.query)
            val whereGenres = filters.selectedGenres.isEmpty() ||
                    item.genres.any { it in filters.selectedGenres }
            val whereTags = filters.selectedTags.isEmpty() ||
                    item.tags.any { it in filters.selectedTags }
            val whereCurrentScoreRange = filters.currentUserScoreRange?.let { scoreRange ->
                scoreFormat.formatValue(
                    score = item.currentUserScore?.userScore?.toFloat() ?: 0f
                ) in scoreRange
            } ?: true
            val whereTargetScoreRange = filters.targetUserScoreRange?.let { scoreRange ->
                scoreFormat.formatValue(
                    score = item.targetUserScore?.userScore?.toFloat() ?: 0f
                ) in scoreRange
            } ?: true

            whereTitle && whereGenres && whereTags && whereCurrentScoreRange && whereTargetScoreRange
        }
    }

    private fun MediaComparison.matchesQuery(query: String): Boolean {
        return title?.matchesTitle(query) == true || synonyms.any { it.contains(query, ignoreCase = true) }
    }

    private fun MediaTitle.matchesTitle(query: String): Boolean {
        return romaji.contains(query, ignoreCase = true) ||
                english?.contains(query, ignoreCase = true) == true ||
                russian?.contains(query, ignoreCase = true) == true ||
                native?.contains(query, ignoreCase = true) == true
    }
}