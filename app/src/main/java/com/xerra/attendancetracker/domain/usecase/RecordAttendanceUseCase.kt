package com.xerra.attendancetracker.domain.usecase

import com.xerra.attendancetracker.domain.model.AttendanceHistory
import com.xerra.attendancetracker.domain.repository.HistoryRepository
import com.xerra.attendancetracker.domain.repository.SubjectRepository

class RecordAttendanceUseCase(
    private val subjectRepository: SubjectRepository,
    private val historyRepository: HistoryRepository
) {
    suspend operator fun invoke(subjectId: Long, isPresent: Boolean): Result<Unit> {
        val subject = subjectRepository.getSubjectById(subjectId)
            ?: return Result.failure(IllegalArgumentException("Subject not found."))

        val status = if (isPresent) "PRESENT" else "ABSENT"
        val newPresent = if (isPresent) subject.presentCount + 1 else subject.presentCount
        val newTotal = subject.totalCount + 1

        if (newTotal < 0 || newPresent < 0) {
            return Result.failure(IllegalArgumentException("Attendance counts cannot be negative."))
        }

        val updatedSubject = subject.copy(
            presentCount = newPresent,
            totalCount = newTotal,
            updatedDate = System.currentTimeMillis()
        )
        subjectRepository.updateSubject(updatedSubject)

        val historyEntry = AttendanceHistory(
            subjectId = subject.id,
            subjectName = subject.name,
            status = status,
            timestamp = System.currentTimeMillis()
        )
        historyRepository.insertHistory(historyEntry)

        return Result.success(Unit)
    }
}
