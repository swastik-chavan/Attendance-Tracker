package com.xerra.attendancetracker.ui.settings

import android.app.Application
import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.xerra.attendancetracker.AttendanceApplication
import com.xerra.attendancetracker.domain.usecase.ExportDataUseCase
import com.xerra.attendancetracker.domain.usecase.ImportDataUseCase
import com.xerra.attendancetracker.domain.usecase.ResetAppUseCase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.InputStream
import java.io.OutputStream

class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    private val app = application as AttendanceApplication
    private val exportDataUseCase = ExportDataUseCase(app)
    private val importDataUseCase = ImportDataUseCase(app)
    private val resetAppUseCase = ResetAppUseCase(app.subjectRepository, app.historyRepository)

    private val sharedPrefs = app.getSharedPreferences("attendance_prefs", Context.MODE_PRIVATE)

    private val _themeMode = MutableStateFlow("SYSTEM")
    val themeMode: StateFlow<String> = _themeMode

    private val _uiScale = MutableStateFlow("DEFAULT")
    val uiScale: StateFlow<String> = _uiScale

    private val _targetGoal = MutableStateFlow(75f)
    val targetGoal: StateFlow<Float> = _targetGoal

    private val _settingsEvent = MutableSharedFlow<Result<String>>()
    val settingsEvent: SharedFlow<Result<String>> = _settingsEvent

    init {
        _themeMode.value = sharedPrefs.getString("theme_mode", "SYSTEM") ?: "SYSTEM"
        _uiScale.value = sharedPrefs.getString("ui_scale", "DEFAULT") ?: "DEFAULT"
        _targetGoal.value = sharedPrefs.getFloat("target_attendance_goal", 75f)
    }

    fun setTheme(theme: String) {
        _themeMode.value = theme
        sharedPrefs.edit().putString("theme_mode", theme).apply()
        
        val mode = when (theme) {
            "LIGHT" -> AppCompatDelegate.MODE_NIGHT_NO
            "DARK" -> AppCompatDelegate.MODE_NIGHT_YES
            else -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
        }
        AppCompatDelegate.setDefaultNightMode(mode)
    }

    fun setUiScale(scale: String) {
        _uiScale.value = scale
        sharedPrefs.edit().putString("ui_scale", scale).apply()
        viewModelScope.launch {
            _settingsEvent.emit(Result.success("UI_SCALE_CHANGED"))
        }
    }

    fun setTargetGoal(goal: Float) {
        _targetGoal.value = goal
        sharedPrefs.edit().putFloat("target_attendance_goal", goal).apply()
    }

    fun exportDatabase(outputStream: OutputStream) {
        viewModelScope.launch {
            val result = exportDataUseCase(outputStream)
            if (result.isSuccess) {
                _settingsEvent.emit(Result.success("EXPORT_SUCCESS"))
            } else {
                _settingsEvent.emit(Result.failure(result.exceptionOrNull() ?: Exception("Export failed")))
            }
        }
    }

    fun importDatabase(inputStream: InputStream) {
        viewModelScope.launch {
            val result = importDataUseCase(inputStream)
            if (result.isSuccess) {
                _settingsEvent.emit(Result.success("IMPORT_SUCCESS"))
            } else {
                _settingsEvent.emit(Result.failure(result.exceptionOrNull() ?: Exception("Import failed")))
            }
        }
    }

    fun resetApp() {
        viewModelScope.launch {
            try {
                resetAppUseCase()
                _settingsEvent.emit(Result.success("RESET_SUCCESS"))
            } catch (e: Exception) {
                _settingsEvent.emit(Result.failure(e))
            }
        }
    }
}
