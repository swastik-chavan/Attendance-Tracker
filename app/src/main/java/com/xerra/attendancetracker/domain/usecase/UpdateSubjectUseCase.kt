package com.xerra.attendancetracker.domain.usecase

import com.xerra.attendancetracker.domain.model.Subject
import com.xerra.attendancetracker.domain.repository.SubjectRepository

class UpdateSubjectUseCase(private val subjectRepository: SubjectRepository) {
    suspend operator fun invoke(subject: Subject): Result<Unit> {
        if (subject.name.trim().isEmpty()) {
            return Result.failure(IllegalArgumentException("Subject name cannot be empty."))
        }
        if (subject.name.length > 50) {
            return Result.failure(IllegalArgumentException("Subject name cannot exceed 50 characters."))
        }
        val existing = subjectRepository.getSubjectByName(subject.name.trim())
        if (existing != null && existing.id != subject.id) {
            return Result.failure(IllegalArgumentException("Another subject with name '${subject.name.trim()}' already exists."))
        }
        subjectRepository.updateSubject(subject.copy(name = subject.name.trim()))
        return Result.success(Unit)
    }
}
