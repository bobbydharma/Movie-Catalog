package com.example.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.database.dao.MovieDao
import com.example.database.dao.RemoteKeyDao
import com.example.database.entity.MovieEntity
import com.example.database.entity.RemoteKeyEntity

@Database(
    entities = [
        MovieEntity::class,
        RemoteKeyEntity::class]
    , version = 1, exportSchema = false)
abstract class MovieDatabase : RoomDatabase() {
    abstract fun movieDao(): MovieDao
    abstract fun remoteKeyDao(): RemoteKeyDao
}