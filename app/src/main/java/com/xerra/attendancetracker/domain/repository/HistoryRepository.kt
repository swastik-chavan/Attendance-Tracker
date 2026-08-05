package com.xerra.attendancetracker.domain.repository

import com.xerra.attendancetracker.domain.model.AttendanceHistory
import kotlinx.coroutines.flow.Flow

interface HistoryRepository {
    fun getAllHistory(): Flow<List<AttendanceHistory>>
    suspend fun insertHistory(history: AttendanceHistory): Long
    suspend fun deleteHistoryById(id: Long)
    suspend fun deleteAllHistory()
}
