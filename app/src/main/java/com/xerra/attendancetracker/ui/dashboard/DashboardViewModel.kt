package com.xerra.attendancetracker.ui.dashboard

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.xerra.attendancetracker.AttendanceApplication
import com.xerra.attendancetracker.domain.model.Subject
import com.xerra.attendancetracker.domain.usecase.DeleteSubjectUseCase
import com.xerra.attendancetracker.domain.usecase.GetSubjectsUseCase
import com.xerra.attendancetracker.domain.usecase.RecordAttendanceUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class SortMode {
    NAME_ASC,
    PERCENT_DESC,
    PERCENT_ASC,
    CREATED_DESC
}

class DashboardViewModel(application: Application) : AndroidViewModel(application) {

    private val app = application as AttendanceApplication
    
    private val getSubjectsUseCase = GetSubjectsUseCase(app.subjectRepository)
    private val recordAttendanceUseCase = RecordAttendanceUseCase(app.subjectRepository, app.historyRepository)
    private val deleteSubjectUseCase = DeleteSubjectUseCase(app.subjectRepository)

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _sortMode = MutableStateFlow(SortMode.NAME_ASC)
    val sortMode: StateFlow<SortMode> = _sortMode

    private val _targetGoal = MutableStateFlow(75f)
    val targetGoal: StateFlow<Float> = _targetGoal

    init {
        loadTargetGoal()
    }

    val subjects: StateFlow<List<Subject>> = combine(
        getSubjectsUseCase(),
        _searchQuery,
        _sortMode
    ) { list, query, sort ->
        var filtered = if (query.isEmpty()) {
            list
        } else {
            list.filter { it.name.contains(query, ignoreCase = true) || it.teacherName?.contains(query, ignoreCase = true) == true }
        }
        
        filtered = when (sort) {
            SortMode.NAME_ASC -> filtered.sortedBy { it.name.lowercase() }
            SortMode.PERCENT_DESC -> filtered.sortedByDescending { it.percentage }
            SortMode.PERCENT_ASC -> filtered.sortedBy { it.percentage }
            SortMode.CREATED_DESC -> filtered.sortedByDescending { it.createdDate }
        }
        filtered
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val overallStats = getSubjectsUseCase().combine(_targetGoal) { list, target ->
        var totalPresent = 0
        var totalClasses = 0
        list.forEach {
            totalPresent += it.presentCount
            totalClasses += it.totalCount
        }
        val pct = if (totalClasses == 0) 0.0 else (totalPresent.toDouble() / totalClasses.toDouble() * 100.0)
        Triple(pct, totalPresent, totalClasses)
    }.stateIn(viewModelScope, SharingStarted.Lazily, Triple(0.0, 0, 0))

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setSortMode(mode: SortMode) {
        _sortMode.value = mode
    }

    fun loadTargetGoal() {
        val sharedPrefs = app.getSharedPreferences("attendance_prefs", Context.MODE_PRIVATE)
        _targetGoal.value = sharedPrefs.getFloat("target_attendance_goal", 75f)
    }

    fun recordPresent(subjectId: Long) {
        viewModelScope.launch {
            recordAttendanceUseCase(subjectId, isPresent = true)
        }
    }

    fun recordAbsent(subjectId: Long) {
        viewModelScope.launch {
            recordAttendanceUseCase(subjectId, isPresent = false)
        }
    }

    fun deleteSubject(subject: Subject) {
        viewModelScope.launch {
            deleteSubjectUseCase(subject)
        }
    }
}
