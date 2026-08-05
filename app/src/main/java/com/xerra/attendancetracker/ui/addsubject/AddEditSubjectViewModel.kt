package com.xerra.attendancetracker.ui.addsubject

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.xerra.attendancetracker.AttendanceApplication
import com.xerra.attendancetracker.domain.model.Subject
import com.xerra.attendancetracker.domain.usecase.AddSubjectUseCase
import com.xerra.attendancetracker.domain.usecase.UpdateSubjectUseCase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AddEditSubjectViewModel(application: Application) : AndroidViewModel(application) {

    private val app = application as AttendanceApplication
    private val addSubjectUseCase = AddSubjectUseCase(app.subjectRepository)
    private val updateSubjectUseCase = UpdateSubjectUseCase(app.subjectRepository)
    private val subjectRepository = app.subjectRepository

    private val _subject = MutableStateFlow<Subject?>(null)
    val subject: StateFlow<Subject?> = _subject

    private val _saveResult = MutableSharedFlow<Result<Unit>>()
    val saveResult: SharedFlow<Result<Unit>> = _saveResult

    private val _selectedColor = MutableStateFlow(0)
    val selectedColor: StateFlow<Int> = _selectedColor

    fun loadSubject(id: Long) {
        if (id == -1L) return
        viewModelScope.launch {
            val existing = subjectRepository.getSubjectById(id)
            if (existing != null) {
                _subject.value = existing
                _selectedColor.value = existing.color
            }
        }
    }

    fun selectColor(colorIndex: Int) {
        _selectedColor.value = colorIndex
    }

    fun saveSubject(name: String, teacherName: String?) {
        viewModelScope.launch {
            val currentSubject = _subject.value
            val result = if (currentSubject == null) {
                val newSubject = Subject(
                    name = name,
                    teacherName = teacherName,
                    color = _selectedColor.value,
                    presentCount = 0,
                    totalCount = 0,
                    createdDate = System.currentTimeMillis(),
                    updatedDate = System.currentTimeMillis()
                )
                addSubjectUseCase(newSubject).map { Unit }
            } else {
                val updatedSubject = currentSubject.copy(
                    name = name,
                    teacherName = teacherName,
                    color = _selectedColor.value,
                    updatedDate = System.currentTimeMillis()
                )
                updateSubjectUseCase(updatedSubject)
            }
            _saveResult.emit(result)
        }
    }
}
