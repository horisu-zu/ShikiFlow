package com.example.shikiflow.presentation.viewmodel.user.statistics

import com.example.shikiflow.domain.model.tracks.MediaType
import com.example.shikiflow.presentation.screen.more.profile.stats.StatsBarType
import com.example.shikiflow.presentation.screen.more.profile.stats.UserStatsSectionType

interface UserStatsEvent {
    fun setUserId(userId: Int)

    fun setMediaType(mediaType: MediaType)
    fun setTypesList(typesList: List<MediaType>)

    fun setStatsSectionType(statsSectionType: UserStatsSectionType)

    fun setScoreBarType(scoreBarType: StatsBarType)
    fun setLengthBarType(lengthBarType: StatsBarType)
    fun setReleaseYearBarType(releaseYearBarType: StatsBarType)
    fun setStartYearBarType(startYearBarType: StatsBarType)
    fun setGenresBarType(genresBarType: StatsBarType)
    fun setTagsBarType(tagsBarType: StatsBarType)
    fun setStaffBarType(staffBarType: StatsBarType)
    fun setVoiceActorsBarType(voiceActorsBarType: StatsBarType)
    fun setStudiosBarType(studiosBarType: StatsBarType)

    fun onRefresh()
}