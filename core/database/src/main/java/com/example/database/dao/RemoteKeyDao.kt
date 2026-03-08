package com.example.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.database.entity.RemoteKeyEntity

@Dao
interface RemoteKeyDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertKey(remoteKey: RemoteKeyEntity)

    @Query("SELECT * FROM remote_keys WHERE id = :id")
    suspend fun getRemoteKey(id: String): RemoteKeyEntity?

    @Query("DELETE FROM remote_keys WHERE id = :id")
    suspend fun clearRemoteKeys(id: String)
}