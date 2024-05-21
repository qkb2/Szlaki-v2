package com.example.listdetailtest

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase

@Entity(tableName = "recorded_times")
data class RecordedTime(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val stopwatchId: Int,
    val timeInSeconds: Int
)

@Dao
interface RecordedTimeDao {
    @Query("SELECT * FROM recorded_times WHERE stopwatchId = :stopwatchId")
    suspend fun getTimesForStopwatch(stopwatchId: Int): List<RecordedTime>

    @Insert
    suspend fun insert(recordedTime: RecordedTime)
}

@Database(entities = [RecordedTime::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun recordedTimeDao(): RecordedTimeDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
