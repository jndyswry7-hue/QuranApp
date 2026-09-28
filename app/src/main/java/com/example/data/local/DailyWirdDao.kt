package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.DailyWird
import kotlinx.coroutines.flow.Flow

@Dao
interface DailyWirdDao {
    @Query("SELECT * FROM daily_wird WHERE dateStr = :dateStr LIMIT 1")
    fun getWirdForDate(dateStr: String): Flow<DailyWird?>

    @Query("SELECT * FROM daily_wird ORDER BY dateStr DESC LIMIT 30")
    fun getRecentWirds(): Flow<List<DailyWird>>

    @Query("SELECT COUNT(*) FROM daily_wird WHERE isCompleted = 1")
    fun getCompletedStreakCount(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateWird(wird: DailyWird)

    @Query("UPDATE daily_wird SET readPages = :pages, isCompleted = :completed, lastUpdated = :now WHERE dateStr = :dateStr")
    suspend fun updatePages(dateStr: String, pages: Int, completed: Boolean, now: Long = System.currentTimeMillis())
}
