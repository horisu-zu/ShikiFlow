package com.example.shikiflow.data.local

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.shikiflow.data.local.converter.GenreConverter
import com.example.shikiflow.data.local.converter.InstantConverter
import com.example.shikiflow.data.local.converter.ListConverter
import com.example.shikiflow.data.local.converter.MediaTitleConverter
import com.example.shikiflow.data.local.dao.MediaTracksDao
import com.example.shikiflow.data.local.dao.RemoteKeysDao
import com.example.shikiflow.data.local.dao.ThreadCommentsDao
import com.example.shikiflow.data.local.entity.keys.RemoteKey
import com.example.shikiflow.data.local.entity.mediatrack.MediaShortEntity
import com.example.shikiflow.data.local.entity.mediatrack.MediaTrackEntity
import com.example.shikiflow.data.local.entity.thread_comment.ThreadCommentEntity
import com.example.shikiflow.data.local.entity.thread_comment.ThreadEntity
import com.example.shikiflow.data.local.entity.thread_comment.UserShortEntity

@Database(
    version = 5,
    entities = [
        MediaTrackEntity::class,
        MediaShortEntity::class,
        ThreadCommentEntity::class,
        UserShortEntity::class,
        ThreadEntity::class,
        RemoteKey::class
    ],
    autoMigrations = [
        AutoMigration(from = 1, to = 2),
        AutoMigration(from = 2, to = 3),
        AutoMigration(from = 3, to = 4),
        AutoMigration(from = 4, to = 5)
    ],
    exportSchema = true
)
@TypeConverters(
    InstantConverter::class,
    ListConverter::class,
    MediaTitleConverter::class,
    GenreConverter::class
)
abstract class AppRoomDatabase : RoomDatabase() {
    abstract fun mediaTracksDao(): MediaTracksDao
    abstract fun threadCommentsDao(): ThreadCommentsDao
    abstract fun remoteKeysDao(): RemoteKeysDao
}