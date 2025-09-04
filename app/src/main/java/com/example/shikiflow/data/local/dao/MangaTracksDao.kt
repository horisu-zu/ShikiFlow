package com.example.shikiflow.data.local.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.shikiflow.data.local.entity.mangatrack.MangaShortEntity
import com.example.shikiflow.data.local.entity.mangatrack.MangaTrackDto
import com.example.shikiflow.data.local.entity.mangatrack.MangaTrackEntity

@Dao
interface MangaTracksDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTracks(tracks: List<MangaTrackEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMangaItems(animeItems: List<MangaShortEntity>)

    @Insert
    suspend fun insert(track: MangaTrackEntity)

    @Delete
    fun deleteTrack(track: MangaTrackEntity)

    @Query("DELETE FROM manga_track WHERE status = :status")
    fun clearTracks(status: String)

    @Query("DELETE FROM manga_short WHERE status = :status")
    fun clearMangaItems(status: String)

    @Transaction
    @Query("SELECT * FROM manga_track WHERE status = :status ORDER BY updatedAt DESC")
    fun getTracksByStatus(status: String): PagingSource<Int, MangaTrackDto>
}