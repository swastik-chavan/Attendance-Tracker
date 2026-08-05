package com.xerra.attendancetracker

import com.xerra.attendancetracker.domain.model.Subject
import com.xerra.attendancetracker.domain.repository.SubjectRepository
import com.xerra.attendancetracker.domain.usecase.AddSubjectUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertTrue
import org.junit.Test

class MockSubjectRepository : SubjectRepository {
    private val list = mutableListOf<Subject>()

    override fun getAllSubjects(): Flow<List<Subject>> = flowOf(list)

    override suspend fun getSubjectById(id: Long): Subject? {
        return list.find { it.id == id }
    }

    override suspend fun getSubjectByName(name: String): Subject? {
        return list.find { it.name.equals(name, ignoreCase = true) }
    }

    override suspend fun insertSubject(subject: Subject): Long {
        val newId = (list.size + 1).toLong()
        list.add(subject.copy(id = newId))
        return newId
    }

    override suspend fun updateSubject(subject: Subject) {
        val index = list.indexOfFirst { it.id == subject.id }
        if (index != -1) {
            list[index] = subject
        }
    }

    override suspend fun deleteSubject(subject: Subject) {
        list.removeIf { it.id == subject.id }
    }
}

class UseCasesTest {

    @Test
    fun testAddSubjectSuccess() = runBlocking {
        val repo = MockSubjectRepository()
        val addSubjectUseCase = AddSubjectUseCase(repo)

        val subject = Subject(
            name = "Biology",
            teacherName = "Mendel",
            color = 3,
            presentCount = 0,
            totalCount = 0,
            createdDate = 0,
            updatedDate = 0
        )
        val result = addSubjectUseCase(subject)
        assertTrue(result.isSuccess)
    }

    @Test
    fun testAddSubjectEmptyName() = runBlocking {
        val repo = MockSubjectRepository()
        val addSubjectUseCase = AddSubjectUseCase(repo)

        val subject = Subject(
            name = "   ",
            teacherName = "Mendel",
            color = 3,
            presentCount = 0,
            totalCount = 0,
            createdDate = 0,
            updatedDate = 0
        )
        val result = addSubjectUseCase(subject)
        assertTrue(result.isFailure)
    }
}
