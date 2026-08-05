package com.xerra.attendancetracker.domain.usecase

import com.xerra.attendancetracker.domain.repository.HistoryRepository
import com.xerra.attendancetracker.domain.repository.SubjectRepository
import kotlinx.coroutines.flow.first

class ResetAppUseCase(
    private val subjectRepository: SubjectRepository,
    private val historyRepository: HistoryRepository
) {
    suspend operator fun invoke() {
        historyRepository.deleteAllHistory()
        try {
            val subjects = subjectRepository.getAllSubjects().first()
            subjects.forEach {
                subjectRepository.deleteSubject(it)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
