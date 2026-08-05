package com.xerra.attendancetracker.domain.usecase

import com.xerra.attendancetracker.domain.model.Subject
import com.xerra.attendancetracker.domain.repository.SubjectRepository

class DeleteSubjectUseCase(private val subjectRepository: SubjectRepository) {
    suspend operator fun invoke(subject: Subject) {
        subjectRepository.deleteSubject(subject)
    }
}
