package com.example.shikiflow.data.local.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.shikiflow.data.local.entity.animetrack.AnimeShortEntity
import com.example.shikiflow.data.local.entity.animetrack.AnimeTrackDto
import com.example.shikiflow.data.local.entity.animetrack.AnimeTrackEntity

@Dao
interface AnimeTracksDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTracks(tracks: List<AnimeTrackEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAnimeItems(animeItems: List<AnimeShortEntity>)

    @Insert
    suspend fun insert(track: AnimeTrackEntity)

    @Delete
    fun deleteTrack(track: AnimeTrackEntity)

    @Query("DELETE FROM anime_track WHERE status = :status")
    fun clearTracks(status: String)

    @Query("DELETE FROM anime_short WHERE status = :status")
    fun clearAnimeItems(status: String)

    @Query("SELECT * FROM anime_track WHERE status = :status ORDER BY updatedAt DESC")
    fun getTracksByStatus(status: String): PagingSource<Int, AnimeTrackDto>
}