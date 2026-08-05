package com.xerra.attendancetracker.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.xerra.attendancetracker.data.database.dao.AttendanceHistoryDao
import com.xerra.attendancetracker.data.database.dao.SubjectDao
import com.xerra.attendancetracker.data.database.entity.AttendanceHistoryEntity
import com.xerra.attendancetracker.data.database.entity.SubjectEntity

@Database(
    entities = [SubjectEntity::class, AttendanceHistoryEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun subjectDao(): SubjectDao
    abstract fun attendanceHistoryDao(): AttendanceHistoryDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "attendance_tracker_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
