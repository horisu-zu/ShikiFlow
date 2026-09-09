package com.example.shikiflow.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.shikiflow.data.local.entity.episode.EpisodeNotificationEntity
import com.example.shikiflow.domain.model.episode_notification.EpisodeNotificationType
import kotlin.time.Instant

@Dao
interface EpisodeNotificationsDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(entity: EpisodeNotificationEntity)

    @Query("""
        SELECT EXISTS
        (SELECT 1 FROM episode_notifications 
        WHERE mediaId = :mediaId AND episode = :episode AND type = :type)
    """)
    suspend fun notified(mediaId: Int, episode: Int, type: EpisodeNotificationType): Boolean

    @Query("DELETE FROM episode_notifications WHERE notifiedAt < :instant")
    suspend fun deleteOlderThan(instant: Instant)
}