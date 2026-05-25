package com.example.shikiflow.data.local.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RawQuery
import androidx.room.RoomRawQuery
import androidx.room.Transaction
import androidx.room.Update
import com.example.shikiflow.data.local.entity.mediatrack.MediaShortEntity
import com.example.shikiflow.data.local.entity.mediatrack.MediaTrackDto
import com.example.shikiflow.data.local.entity.mediatrack.MediaTrackEntity
import com.example.shikiflow.domain.model.tracks.MediaType
import kotlinx.coroutines.flow.Flow

@Dao
interface MediaTracksDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTracks(tracks: List<MediaTrackEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItems(items: List<MediaShortEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrack(track: MediaTrackEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertShortEntity(anime: MediaShortEntity)

    @Query("""
        DELETE FROM media_track
        WHERE id IN (
            SELECT media_track.id FROM media_track
            INNER JOIN media_short ON media_short.id = media_track.mediaId
            WHERE media_track.status = :status
            AND media_short.mediaType = :mediaType
            ORDER BY media_track.updatedAt DESC
            LIMIT :limit
        )
    """)
    suspend fun clearTracksByStatus(status: String, mediaType: MediaType, limit: Int)

    @Query("""
        DELETE FROM media_short
        WHERE id IN (
            SELECT media_short.id FROM media_short
            INNER JOIN media_track ON media_track.mediaId = media_short.id
            WHERE media_short.status = :status
            AND media_short.mediaType = :mediaType
            ORDER BY media_track.updatedAt DESC
            LIMIT :limit
        )
    """)
    suspend fun clearItemsByStatus(status: String, mediaType: MediaType, limit: Int)

    @Query("DELETE FROM media_track WHERE id = :entryId")
    suspend fun deleteTrack(entryId: Int)

    @Query("DELETE FROM media_short WHERE id = :mediaId AND mediaType = :mediaType")
    suspend fun deleteMedia(mediaId: Int, mediaType: MediaType)

    @Update
    suspend fun updateTrack(track: MediaTrackEntity)

    @Query("""
        SELECT * FROM media_track
        INNER JOIN media_short ON media_track.mediaId = media_short.id
        WHERE media_short.id = :id
        AND media_short.mediaType = :mediaType
    """)
    fun getTrackById(id: Int, mediaType: MediaType): Flow<MediaTrackDto?>

    @Transaction
    @Query("""
        SELECT * FROM media_track
        INNER JOIN media_short ON media_track.mediaId = media_short.id
        WHERE media_track.status = :status 
        AND media_short.mediaType = :mediaType
        ORDER BY updatedAt DESC
    """)
    fun getTracksByStatus(
        status: String,
        mediaType: MediaType
    ): PagingSource<Int, MediaTrackDto>

    @RawQuery(observedEntities = [MediaTrackDto::class, MediaShortEntity::class])
    fun browseMediaTracks(query: RoomRawQuery): PagingSource<Int, MediaTrackDto>

    @Query("""
        SELECT COUNT(*) == 0 FROM media_track
        INNER JOIN media_short ON media_track.mediaId = media_short.id
        WHERE mediaType = :mediaType
    """)
    suspend fun isEmpty(mediaType: MediaType): Boolean
}