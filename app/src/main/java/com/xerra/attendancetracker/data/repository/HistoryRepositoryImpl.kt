package com.xerra.attendancetracker.data.repository

import com.xerra.attendancetracker.data.database.dao.AttendanceHistoryDao
import com.xerra.attendancetracker.domain.model.AttendanceHistory
import com.xerra.attendancetracker.domain.repository.HistoryRepository
import com.xerra.attendancetracker.data.mapper.toDomain
import com.xerra.attendancetracker.data.mapper.toEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class HistoryRepositoryImpl(private val historyDao: AttendanceHistoryDao) : HistoryRepository {
    override fun getAllHistory(): Flow<List<AttendanceHistory>> {
        return historyDao.getAllHistory().map { list ->
            list.map { it.toDomain() }
        }
    }

    override suspend fun insertHistory(history: AttendanceHistory): Long {
        return historyDao.insert(history.toEntity())
    }

    override suspend fun deleteHistoryById(id: Long) {
        historyDao.deleteById(id)
    }

    override suspend fun deleteAllHistory() {
        historyDao.deleteAll()
    }
}
