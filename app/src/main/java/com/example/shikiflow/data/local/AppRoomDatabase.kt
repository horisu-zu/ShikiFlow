package com.example.shikiflow.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.shikiflow.data.local.converter.InstantConverter
import com.example.shikiflow.data.local.dao.AnimeTracksDao
import com.example.shikiflow.data.local.dao.MangaTracksDao
import com.example.shikiflow.data.local.entity.animetrack.AnimeShortEntity
import com.example.shikiflow.data.local.entity.animetrack.AnimeTrackEntity
import com.example.shikiflow.data.local.entity.mangatrack.MangaShortEntity
import com.example.shikiflow.data.local.entity.mangatrack.MangaTrackEntity

@Database(
    entities = [AnimeTrackEntity::class, AnimeShortEntity::class,
        MangaTrackEntity::class, MangaShortEntity::class,
        //VersionEntity::class, AssetsEntity::class
        ],
    version = 2,
    exportSchema = false
)
@TypeConverters(InstantConverter::class)
abstract class AppRoomDatabase : RoomDatabase() {
    abstract fun animeTracksDao(): AnimeTracksDao
    abstract fun mangaTracksDao(): MangaTracksDao
    //abstract fun versionDao(): VersionDao
}