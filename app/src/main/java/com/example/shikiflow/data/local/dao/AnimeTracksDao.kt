package com.example.shikiflow.data.local.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.shikiflow.data.local.entity.animetrack.AnimeShortEntity
import com.example.shikiflow.data.local.entity.animetrack.AnimeTrackDto
import com.example.shikiflow.data.local.entity.animetrack.AnimeTrackEntity

@Dao
interface AnimeTracksDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTracks(tracks: List<AnimeTrackEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAnimeItems(animeItems: List<AnimeShortEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrack(track: AnimeTrackEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertShortEntity(anime: AnimeShortEntity)

    @Delete
    suspend fun deleteTrack(track: AnimeTrackEntity)

    @Query("DELETE FROM anime_track WHERE status = :status")
    fun clearTracksByStatus(status: String)

    @Query("DELETE FROM anime_short WHERE status = :status")
    fun clearAnimeItemsByStatus(status: String)

    @Update
    suspend fun updateTrack(track: AnimeTrackEntity)

    @Query("SELECT EXISTS(SELECT 1 FROM anime_track WHERE status = :status LIMIT 1)")
    suspend fun hasTracksForStatus(status: String): Boolean

    @Transaction
    @Query("SELECT * FROM anime_track WHERE status = :status ORDER BY updatedAt DESC")
    fun getTracksByStatus(status: String): PagingSource<Int, AnimeTrackDto>
}