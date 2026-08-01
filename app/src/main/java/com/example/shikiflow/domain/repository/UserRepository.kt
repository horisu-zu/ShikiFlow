package com.example.shikiflow.domain.repository

import androidx.paging.PagingData
import com.example.shikiflow.domain.model.auth.AuthType
import com.example.shikiflow.domain.model.browse.Browse
import com.example.shikiflow.domain.model.common.ScoreFormat
import com.example.shikiflow.domain.model.media_details.Genre
import com.example.shikiflow.domain.model.media_details.MediaTagEnum
import com.example.shikiflow.domain.model.media_details.PreferredTitleType
import com.example.shikiflow.domain.model.staff.StaffKind
import com.example.shikiflow.domain.model.thread.Like
import com.example.shikiflow.domain.model.thread.LikeableType
import com.example.shikiflow.domain.model.user.FavoriteCategory
import com.example.shikiflow.domain.model.tracks.MediaType
import com.example.shikiflow.domain.model.user.User
import com.example.shikiflow.domain.model.user.UserFavorite
import com.example.shikiflow.domain.model.user.stats.OverviewStats
import com.example.shikiflow.domain.model.tracks.ShortUserMediaRate
import com.example.shikiflow.domain.model.user.social.SocialCategory
import com.example.shikiflow.domain.model.user.activity.UserActivity
import com.example.shikiflow.domain.model.user.UserFollow
import com.example.shikiflow.domain.model.user.UserSettings
import com.example.shikiflow.domain.model.user.stats.TypeStat
import com.example.shikiflow.domain.model.user.stats.MediaTypeStats
import com.example.shikiflow.domain.model.user.stats.StaffStat
import com.example.shikiflow.domain.model.user.UserStatsCategories
import com.example.shikiflow.domain.model.user.activity.ActivityReply
import com.example.shikiflow.domain.model.user.social.UserSocial
import com.example.shikiflow.domain.model.user.stats.StudioStat
import com.example.shikiflow.utils.result.DataResult
import com.example.shikiflow.utils.result.PagedResult
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    fun fetchCurrentUser(authType: AuthType): Flow<DataResult<User>>

    fun getUserActivity(
        userId: Int,
        page: Int,
        limit: Int = 20
    ): Flow<PagedResult<UserActivity>>

    fun getSingleActivity(
        activityId: Int
    ): Flow<DataResult<UserActivity>>

    fun getActivityReplies(
        activityId: Int,
        page: Int,
        limit: Int = 15
    ): Flow<PagedResult<ActivityReply>>

    suspend fun submitActivityReply(
        id: Int?,
        activityId: Int,
        body: String
    ): DataResult<ActivityReply>

    suspend fun deleteActivityReply(
        id: Int
    ): DataResult<Boolean>

    suspend fun toggleLike(
        id: Int,
        type: LikeableType
    ): DataResult<Like>

    fun getUserRates(userId: Int): Flow<DataResult<MediaTypeStats<OverviewStats>>>

    fun getUserGenres(userId: Int): Flow<DataResult<MediaTypeStats<List<TypeStat<Genre>>>>>

    fun getUserTags(userId: Int): Flow<DataResult<MediaTypeStats<List<TypeStat<MediaTagEnum>>>>>

    fun getUserStaff(userId: Int): Flow<DataResult<MediaTypeStats<List<StaffStat>>>>

    fun getUserVoiceActors(userId: Int): Flow<DataResult<List<StaffStat>>>

    fun getUserStudios(userId: Int): Flow<DataResult<List<StudioStat>>>

    suspend fun getUserStatsCategories(
        userId: Int,
        isRefresh: Boolean
    ): DataResult<UserStatsCategories>

    fun getUserSocial(
        userId: Int,
        socialCategory: SocialCategory,
        page: Int,
        limit: Int = 18
    ): Flow<PagedResult<UserSocial>>

    fun getUserFavorites(
        userId: Int,
        favoriteCategory: FavoriteCategory
    ): Flow<PagingData<UserFavorite>>

    fun getFavorites(
        userId: Int,
        favoriteCategory: FavoriteCategory
    ): Flow<DataResult<List<UserFavorite>>>

    suspend fun getMediaRates(userId: Int, mediaType: MediaType): List<ShortUserMediaRate>

    fun getUsers(
        nickname: String
    ): Flow<PagingData<Browse.User>>

    suspend fun toggleFavorite(
        animeId: Int? = null,
        mangaId: Int? = null,
        characterId: Int? = null,
        staffId: Int? = null,
        staffKind: StaffKind? = null,
        studioId: Int? = null,
        isFavorite: Boolean
    ): DataResult<Unit>

    suspend fun getFollow(userId: Int): DataResult<UserFollow>

    suspend fun toggleFollow(
        userId: Int,
        isFollowing: Boolean
    ): DataResult<Boolean>

    suspend fun setUserSettings(
        preferredTitleType: PreferredTitleType? = null,
        scoreFormat: ScoreFormat? = null
    ): DataResult<UserSettings>
}