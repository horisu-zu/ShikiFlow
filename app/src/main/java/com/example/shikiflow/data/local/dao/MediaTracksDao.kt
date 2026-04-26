package com.example.shikiflow.data.local.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
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
        WHERE status = :status 
        AND EXISTS (
            SELECT 1 FROM media_short 
            WHERE media_short.id = media_track.mediaId 
            AND media_short.mediaType = :mediaType
        )
    """)
    fun clearTracksByStatus(status: String, mediaType: MediaType)

    @Query("DELETE FROM media_short WHERE status = :status AND mediaType = :mediaType")
    fun clearItemsByStatus(status: String, mediaType: MediaType)

    @Query("DELETE FROM media_track WHERE id = :entryId")
    suspend fun deleteTrack(entryId: Int)

    @Query("DELETE FROM media_short WHERE id = :mediaId AND mediaType = :mediaType")
    suspend fun deleteMedia(mediaId: Int, mediaType: MediaType)

    @Update
    suspend fun updateTrack(track: MediaTrackEntity)

    @Query("""
        SELECT * FROM media_track
        INNER JOIN media_short ON media_track.mediaId = media_short.id
        WHERE media_short.malId = :malId
        AND media_short.mediaType = :mediaType
    """)
    fun getTrackByMalId(malId: Int, mediaType: MediaType): Flow<MediaTrackDto?>

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

    /*@Transaction
    @Query("""
        SELECT * FROM media_track 
        INNER JOIN media_short ON media_track.mediaId = media_short.id
        WHERE (:status IS NULL OR media_track.status = :status) 
        AND (trim(:title) = '' OR name LIKE '%' || :title || '%')
        AND media_short.mediaType = :mediaType
        ORDER BY updatedAt DESC
    """)
    fun browseTracks(
        title: String,
        status: String?,
        mediaType: MediaType
    ): PagingSource<Int, MediaTrackDto>*/
}