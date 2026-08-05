package com.xerra.attendancetracker.ui.history

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.xerra.attendancetracker.AttendanceApplication
import com.xerra.attendancetracker.domain.model.AttendanceHistory
import com.xerra.attendancetracker.domain.usecase.DeleteHistoryUseCase
import com.xerra.attendancetracker.domain.usecase.GetHistoryUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HistoryViewModel(application: Application) : AndroidViewModel(application) {

    private val app = application as AttendanceApplication
    private val getHistoryUseCase = GetHistoryUseCase(app.historyRepository)
    private val deleteHistoryUseCase = DeleteHistoryUseCase(app.historyRepository, app.subjectRepository)

    val historyLogs: StateFlow<List<AttendanceHistory>> = getHistoryUseCase()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun deleteLog(id: Long, subjectId: Long, status: String) {
        viewModelScope.launch {
            deleteHistoryUseCase.deleteSingleLog(id, subjectId, status)
        }
    }

    fun clearAllLogs() {
        viewModelScope.launch {
            deleteHistoryUseCase.clearAllHistory()
        }
    }
}
