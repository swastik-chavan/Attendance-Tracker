package com.xerra.attendancetracker

import android.app.Application
import com.xerra.attendancetracker.data.database.AppDatabase
import com.xerra.attendancetracker.data.repository.HistoryRepositoryImpl
import com.xerra.attendancetracker.data.repository.SubjectRepositoryImpl
import com.xerra.attendancetracker.domain.repository.HistoryRepository
import com.xerra.attendancetracker.domain.repository.SubjectRepository

class AttendanceApplication : Application() {

    private val database by lazy { AppDatabase.getDatabase(this) }
    
    val subjectRepository: SubjectRepository by lazy { 
        SubjectRepositoryImpl(database.subjectDao()) 
    }
    
    val historyRepository: HistoryRepository by lazy { 
        HistoryRepositoryImpl(database.attendanceHistoryDao()) 
    }
}
