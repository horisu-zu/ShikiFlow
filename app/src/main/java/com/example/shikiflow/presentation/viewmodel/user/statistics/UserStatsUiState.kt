package com.example.shikiflow.presentation.viewmodel.user.statistics

import com.example.shikiflow.domain.model.auth.AuthType
import com.example.shikiflow.domain.model.tracks.MediaType
import com.example.shikiflow.domain.model.user.stats.MediaTypeStats
import com.example.shikiflow.domain.model.user.stats.OverviewStats
import com.example.shikiflow.domain.model.user.stats.StaffStat
import com.example.shikiflow.domain.model.user.stats.StudioStat
import com.example.shikiflow.domain.model.user.stats.TypeStat
import com.example.shikiflow.presentation.UiState
import com.example.shikiflow.presentation.screen.more.profile.stats.StatsBarType
import com.example.shikiflow.presentation.screen.more.profile.stats.UserStatsSectionType

data class UserStatsUiState(
    val userId: Int? = null,
    val authType: AuthType? = null,
    val mediaType: MediaType = MediaType.ANIME,
    val typesList: List<MediaType> = emptyList(),

    val statsSectionType: UserStatsSectionType = UserStatsSectionType.OVERVIEW,
    val overviewStats: MediaTypeStats<OverviewStats> = MediaTypeStats(),
    val genreStats: MediaTypeStats<List<TypeStat>> = MediaTypeStats(),
    val tagsStats: MediaTypeStats<List<TypeStat>> = MediaTypeStats(),
    val staffStats: MediaTypeStats<List<StaffStat>> = MediaTypeStats(),
    val voiceActorsStats: List<StaffStat> = emptyList(),
    val studiosStats: List<StudioStat> = emptyList(),

    val scoreBarType: Map<MediaType, StatsBarType> = emptyMap(),
    val lengthBarType: Map<MediaType, StatsBarType> = emptyMap(),
    val releaseYearBarType: Map<MediaType, StatsBarType> = emptyMap(),
    val startYearBarType: Map<MediaType, StatsBarType> = emptyMap(),
    val genresBarType: Map<MediaType, StatsBarType> = emptyMap(),
    val tagsBarType: Map<MediaType, StatsBarType> = emptyMap(),
    val staffBarType: Map<MediaType, StatsBarType> = emptyMap(),
    val voiceActorsBarType: StatsBarType = StatsBarType.TITLES,
    val studiosBarType: StatsBarType = StatsBarType.TITLES,

    override val errorMessage: String? = null,
    override val isLoading: Boolean = true,
    val isRefreshing: Boolean = false
): UiState() {
    override fun setError(value: String?) = copy(errorMessage = value)
    override fun setLoading(value: Boolean) = copy(isLoading = value)
}
