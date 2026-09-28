package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.model.DailyWird
import com.example.data.model.FavoriteItem
import com.example.data.model.PracticeSession

@Database(
    entities = [
        PracticeSession::class,
        FavoriteItem::class,
        DailyWird::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun practiceDao(): PracticeDao
    abstract fun favoriteDao(): FavoriteDao
    abstract fun dailyWirdDao(): DailyWirdDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "al_muqri_dhaki.db"
                ).fallbackToDestructiveMigration(dropAllTables = true).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
