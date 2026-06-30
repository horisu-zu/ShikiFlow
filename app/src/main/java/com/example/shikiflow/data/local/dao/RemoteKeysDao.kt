package com.example.shikiflow.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.shikiflow.data.local.entity.keys.RemoteKey

@Dao
interface RemoteKeysDao {

    @Query("SELECT * FROM remote_keys WHERE `key` = :key")
    suspend fun getKey(key: String): RemoteKey?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(remoteKey: RemoteKey)

    @Query("DELETE FROM remote_keys WHERE `key` = :key")
    suspend fun delete(key: String)
}