package com.xerra.attendancetracker.domain.usecase

import com.xerra.attendancetracker.domain.model.Subject
import com.xerra.attendancetracker.domain.repository.SubjectRepository
import kotlinx.coroutines.flow.Flow

class GetSubjectsUseCase(private val subjectRepository: SubjectRepository) {
    operator fun invoke(): Flow<List<Subject>> = subjectRepository.getAllSubjects()
}
