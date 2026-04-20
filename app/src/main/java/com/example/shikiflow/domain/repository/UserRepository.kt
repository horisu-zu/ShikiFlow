package com.example.shikiflow.domain.repository

import androidx.paging.PagingData
import com.example.shikiflow.domain.model.auth.AuthType
import com.example.shikiflow.domain.model.browse.Browse
import com.example.shikiflow.domain.model.user.FavoriteCategory
import com.example.shikiflow.domain.model.tracks.MediaType
import com.example.shikiflow.domain.model.user.User
import com.example.shikiflow.domain.model.user.UserFavorite
import com.example.shikiflow.domain.model.user.stats.OverviewStats
import com.example.shikiflow.domain.model.tracks.ShortUserMediaRate
import com.example.shikiflow.domain.model.user.social.SocialCategory
import com.example.shikiflow.domain.model.user.UserActivity
import com.example.shikiflow.domain.model.user.stats.TypeStat
import com.example.shikiflow.domain.model.user.stats.MediaTypeStats
import com.example.shikiflow.domain.model.user.stats.StaffStat
import com.example.shikiflow.domain.model.user.UserStatsCategories
import com.example.shikiflow.domain.model.user.social.UserSocial
import com.example.shikiflow.domain.model.user.stats.StudioStat
import com.example.shikiflow.utils.DataResult
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    fun fetchCurrentUser(authType: AuthType): Flow<DataResult<User>>

    fun getUserHistory(userId: Int): Flow<PagingData<UserActivity>>

    fun getUserRates(userId: Int): Flow<DataResult<MediaTypeStats<OverviewStats>>>

    fun getUserGenres(userId: Int): Flow<DataResult<MediaTypeStats<List<TypeStat>>>>

    fun getUserTags(userId: Int): Flow<DataResult<MediaTypeStats<List<TypeStat>>>>

    fun getUserStaff(userId: Int): Flow<DataResult<MediaTypeStats<List<StaffStat>>>>

    fun getUserVoiceActors(userId: Int): Flow<DataResult<List<StaffStat>>>

    fun getUserStudios(userId: Int): Flow<DataResult<List<StudioStat>>>

    fun getUserStatsCategories(userId: Int): Flow<DataResult<UserStatsCategories>>

    fun getUserSocial(userId: Int, socialCategory: SocialCategory): Flow<PagingData<UserSocial>>

    fun getUserFavorites(userId: Int, favoriteCategory: FavoriteCategory): Flow<PagingData<UserFavorite>>

    suspend fun getMediaRates(userId: Int, mediaType: MediaType): List<ShortUserMediaRate>

    fun getUsers(
        nickname: String
    ): Flow<PagingData<Browse.User>>

    suspend fun toggleFavorite(
        animeId: Int? = null,
        mangaId: Int? = null,
        characterId: Int? = null,
        staffId: Int? = null,
        studioId: Int? = null,
    ): DataResult<Unit>
}