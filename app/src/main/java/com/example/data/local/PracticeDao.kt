package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.PracticeSession
import kotlinx.coroutines.flow.Flow

@Dao
interface PracticeDao {
    @Query("SELECT * FROM practice_sessions ORDER BY timestamp DESC")
    fun getAllSessions(): Flow<List<PracticeSession>>

    @Query("SELECT * FROM practice_sessions ORDER BY timestamp DESC LIMIT :limit")
    fun getRecentSessions(limit: Int): Flow<List<PracticeSession>>

    @Query("SELECT COUNT(*) FROM practice_sessions")
    fun getSessionCount(): Flow<Int>

    @Query("SELECT AVG(overallScore) FROM practice_sessions")
    fun getAverageOverallScore(): Flow<Double?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: PracticeSession): Long

    @Query("DELETE FROM practice_sessions WHERE id = :id")
    suspend fun deleteSessionById(id: Long)

    @Query("DELETE FROM practice_sessions")
    suspend fun clearAll()
}
