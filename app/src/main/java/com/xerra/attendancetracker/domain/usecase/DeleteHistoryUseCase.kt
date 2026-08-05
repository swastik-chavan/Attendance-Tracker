package com.xerra.attendancetracker.domain.usecase

import com.xerra.attendancetracker.domain.repository.HistoryRepository
import com.xerra.attendancetracker.domain.repository.SubjectRepository
import kotlinx.coroutines.flow.first

class DeleteHistoryUseCase(
    private val historyRepository: HistoryRepository,
    private val subjectRepository: SubjectRepository
) {
    suspend fun deleteSingleLog(historyId: Long, subjectId: Long, status: String) {
        historyRepository.deleteHistoryById(historyId)

        val subject = subjectRepository.getSubjectById(subjectId)
        if (subject != null) {
            val isPresent = status.equals("PRESENT", ignoreCase = true)
            val newPresent = if (isPresent) (subject.presentCount - 1).coerceAtLeast(0) else subject.presentCount
            val newTotal = (subject.totalCount - 1).coerceAtLeast(newPresent)
            subjectRepository.updateSubject(
                subject.copy(
                    presentCount = newPresent,
                    totalCount = newTotal,
                    updatedDate = System.currentTimeMillis()
                )
            )
        }
    }

    suspend fun clearAllHistory() {
        historyRepository.deleteAllHistory()
        // Reset all subject counts back to zero when clear all history is clicked, keeping data consistent
        try {
            val subjects = subjectRepository.getAllSubjects().first()
            subjects.forEach { subject ->
                if (subject.totalCount > 0) {
                    subjectRepository.updateSubject(
                        subject.copy(
                            presentCount = 0,
                            totalCount = 0,
                            updatedDate = System.currentTimeMillis()
                        )
                    )
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
