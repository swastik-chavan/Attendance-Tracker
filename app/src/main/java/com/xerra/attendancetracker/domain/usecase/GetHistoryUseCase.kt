package com.xerra.attendancetracker.domain.usecase

import com.xerra.attendancetracker.domain.model.AttendanceHistory
import com.xerra.attendancetracker.domain.repository.HistoryRepository
import kotlinx.coroutines.flow.Flow

class GetHistoryUseCase(private val historyRepository: HistoryRepository) {
    operator fun invoke(): Flow<List<AttendanceHistory>> = historyRepository.getAllHistory()
}
