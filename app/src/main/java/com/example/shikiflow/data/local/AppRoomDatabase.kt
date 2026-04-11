package com.example.shikiflow.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.shikiflow.data.local.converter.InstantConverter
import com.example.shikiflow.data.local.converter.ListConverter
import com.example.shikiflow.data.local.dao.MediaTracksDao
import com.example.shikiflow.data.local.entity.mediatrack.MediaShortEntity
import com.example.shikiflow.data.local.entity.mediatrack.MediaTrackEntity

@Database(
    entities = [
        MediaTrackEntity::class,
        MediaShortEntity::class
    ],
    version = 3,
    exportSchema = false
)
@TypeConverters(
    InstantConverter::class,
    ListConverter::class
)
abstract class AppRoomDatabase : RoomDatabase() {
    abstract fun mediaTracksDao(): MediaTracksDao
}