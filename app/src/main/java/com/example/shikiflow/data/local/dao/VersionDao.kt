package com.example.shikiflow.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.shikiflow.data.local.entity.version.AssetsEntity
import com.example.shikiflow.data.local.entity.version.VersionDto
import com.example.shikiflow.data.local.entity.version.VersionEntity
import kotlinx.coroutines.flow.Flow

@Dao
abstract class VersionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertVersionEntity(versionEntity: VersionEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertAssets(assets: List<AssetsEntity>)

    @Transaction
    open suspend fun insertVersion(versionDto: VersionDto) {
        insertVersionEntity(versionDto.version)
        insertAssets(versionDto.assets)
    }

    @Transaction
    @Query("SELECT * FROM version WHERE versionTag = :localVersionTag")
    abstract suspend fun getLatestLocalVersion(localVersionTag: String): VersionDto?

    @Query("SELECT * FROM version WHERE versionTag = :versionTag LIMIT 1")
    abstract fun observeReleaseByVersion(versionTag: String): Flow<VersionDto?>
}