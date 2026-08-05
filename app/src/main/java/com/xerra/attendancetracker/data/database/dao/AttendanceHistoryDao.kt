package com.xerra.attendancetracker.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.xerra.attendancetracker.data.database.entity.AttendanceHistoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AttendanceHistoryDao {
    @Insert
    suspend fun insert(history: AttendanceHistoryEntity): Long

    @Query("SELECT * FROM attendance_history ORDER BY timestamp DESC")
    fun getAllHistory(): Flow<List<AttendanceHistoryEntity>>

    @Query("DELETE FROM attendance_history")
    suspend fun deleteAll()

    @Query("DELETE FROM attendance_history WHERE id = :id")
    suspend fun deleteById(id: Long)
}
