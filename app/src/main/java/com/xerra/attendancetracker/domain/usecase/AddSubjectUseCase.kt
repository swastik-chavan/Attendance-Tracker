package com.xerra.attendancetracker.domain.usecase

import com.xerra.attendancetracker.domain.model.Subject
import com.xerra.attendancetracker.domain.repository.SubjectRepository

class AddSubjectUseCase(private val subjectRepository: SubjectRepository) {
    suspend operator fun invoke(subject: Subject): Result<Long> {
        if (subject.name.trim().isEmpty()) {
            return Result.failure(IllegalArgumentException("Subject name cannot be empty."))
        }
        if (subject.name.length > 50) {
            return Result.failure(IllegalArgumentException("Subject name cannot exceed 50 characters."))
        }
        val existing = subjectRepository.getSubjectByName(subject.name.trim())
        if (existing != null) {
            return Result.failure(IllegalArgumentException("Subject '${subject.name.trim()}' already exists."))
        }
        return Result.success(subjectRepository.insertSubject(subject.copy(name = subject.name.trim())))
    }
}
