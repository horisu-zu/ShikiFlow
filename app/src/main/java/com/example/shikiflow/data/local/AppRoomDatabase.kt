package com.example.shikiflow.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.shikiflow.data.local.converter.InstantConverter
import com.example.shikiflow.data.local.dao.AnimeTracksDao
import com.example.shikiflow.data.local.entity.animetrack.AnimeShortEntity
import com.example.shikiflow.data.local.entity.animetrack.AnimeTrackEntity

@Database(
    entities = [AnimeTrackEntity::class, AnimeShortEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(InstantConverter::class)
abstract class AppRoomDatabase : RoomDatabase() {
    abstract fun animeTracksDao(): AnimeTracksDao
}