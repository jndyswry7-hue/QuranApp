package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.FavoriteItem
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteDao {
    @Query("SELECT * FROM favorites ORDER BY timestamp DESC")
    fun getAllFavorites(): Flow<List<FavoriteItem>>

    @Query("SELECT * FROM favorites WHERE itemType = :type ORDER BY timestamp DESC")
    fun getFavoritesByType(type: String): Flow<List<FavoriteItem>>

    @Query("SELECT EXISTS(SELECT 1 FROM favorites WHERE id = :id)")
    fun isFavorite(id: String): Flow<Boolean>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(item: FavoriteItem)

    @Query("DELETE FROM favorites WHERE id = :id")
    suspend fun deleteFavoriteById(id: String)
}
