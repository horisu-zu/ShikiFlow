package com.example.shikiflow.presentation.viewmodel.user.statistics

import com.example.shikiflow.domain.model.tracks.MediaType
import com.example.shikiflow.presentation.screen.more.profile.stats.StatsBarType
import com.example.shikiflow.presentation.screen.more.profile.stats.UserStatsSectionType

interface UserStatsEvent {
    fun setUserId(userId: Int)

    fun setStatsSectionType(statsSectionType: UserStatsSectionType)

    fun setScoreBarType(mediaType: MediaType, scoreBarType: StatsBarType)
    fun setLengthBarType(mediaType: MediaType, lengthBarType: StatsBarType)
    fun setReleaseYearBarType(mediaType: MediaType, releaseYearBarType: StatsBarType)
    fun setStartYearBarType(mediaType: MediaType, startYearBarType: StatsBarType)

    fun onRefresh()
}