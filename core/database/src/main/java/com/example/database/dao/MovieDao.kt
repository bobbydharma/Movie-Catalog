package com.example.database.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.database.entity.MovieEntity

@Dao
interface MovieDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(movies: List<MovieEntity>)

    @Query("SELECT * FROM movies WHERE category = :category ORDER BY addedAt ASC")
    fun getMoviesByCategory(category: String): PagingSource<Int, MovieEntity>

    @Query("DELETE FROM movies WHERE category = :category")
    suspend fun clearMoviesByCategory(category: String)
}