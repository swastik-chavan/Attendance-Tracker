package com.xerra.attendancetracker.data.repository

import com.xerra.attendancetracker.data.database.dao.SubjectDao
import com.xerra.attendancetracker.domain.model.Subject
import com.xerra.attendancetracker.domain.repository.SubjectRepository
import com.xerra.attendancetracker.data.mapper.toDomain
import com.xerra.attendancetracker.data.mapper.toEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SubjectRepositoryImpl(private val subjectDao: SubjectDao) : SubjectRepository {
    override fun getAllSubjects(): Flow<List<Subject>> {
        return subjectDao.getAllSubjects().map { list ->
            list.map { it.toDomain() }
        }
    }

    override suspend fun getSubjectById(id: Long): Subject? {
        return subjectDao.getSubjectById(id)?.toDomain()
    }

    override suspend fun getSubjectByName(name: String): Subject? {
        return subjectDao.getSubjectByName(name)?.toDomain()
    }

    override suspend fun insertSubject(subject: Subject): Long {
        return subjectDao.insert(subject.toEntity())
    }

    override suspend fun updateSubject(subject: Subject) {
        subjectDao.update(subject.toEntity())
    }

    override suspend fun deleteSubject(subject: Subject) {
        subjectDao.delete(subject.toEntity())
    }
}
