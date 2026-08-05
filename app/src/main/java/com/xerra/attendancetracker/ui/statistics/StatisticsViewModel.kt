package com.xerra.attendancetracker.ui.statistics

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.xerra.attendancetracker.AttendanceApplication
import com.xerra.attendancetracker.domain.usecase.GetSubjectsUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

data class StatsState(
    val overallPercentage: Double = 0.0,
    val totalClasses: Int = 0,
    val presentClasses: Int = 0,
    val absentClasses: Int = 0,
    val totalSubjects: Int = 0,
    val bestSubjectName: String = "N/A",
    val bestSubjectPercentage: Double = 0.0,
    val worstSubjectName: String = "N/A",
    val worstSubjectPercentage: Double = 0.0
)

class StatisticsViewModel(application: Application) : AndroidViewModel(application) {

    private val app = application as AttendanceApplication
    private val getSubjectsUseCase = GetSubjectsUseCase(app.subjectRepository)

    val statsState: StateFlow<StatsState> = getSubjectsUseCase().map { subjects ->
        if (subjects.isEmpty()) {
            StatsState()
        } else {
            var totalPresent = 0
            var totalClasses = 0
            var totalAbsent = 0
            
            var bestSubName = "N/A"
            var bestSubPct = -1.0
            
            var worstSubName = "N/A"
            var worstSubPct = 101.0

            subjects.forEach { subject ->
                totalPresent += subject.presentCount
                totalClasses += subject.totalCount
                totalAbsent += (subject.totalCount - subject.presentCount)

                // Check for highest/best subject
                if (subject.percentage > bestSubPct) {
                    bestSubPct = subject.percentage
                    bestSubName = subject.name
                }

                // Check for lowest/worst subject
                if (subject.percentage < worstSubPct) {
                    worstSubPct = subject.percentage
                    worstSubName = subject.name
                }
            }

            val overallPct = if (totalClasses == 0) 0.0 else (totalPresent.toDouble() / totalClasses.toDouble() * 100.0)

            StatsState(
                overallPercentage = overallPct,
                totalClasses = totalClasses,
                presentClasses = totalPresent,
                absentClasses = totalAbsent,
                totalSubjects = subjects.size,
                bestSubjectName = bestSubName,
                bestSubjectPercentage = if (bestSubPct < 0) 0.0 else bestSubPct,
                worstSubjectName = worstSubName,
                worstSubjectPercentage = if (worstSubPct > 100) 0.0 else worstSubPct
            )
        }
    }.stateIn(viewModelScope, SharingStarted.Lazily, StatsState())
}
