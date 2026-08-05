package com.xerra.attendancetracker.domain.model

data class AttendanceHistory(
    val id: Long = 0,
    val subjectId: Long,
    val subjectName: String,
    val status: String, // "PRESENT" or "ABSENT"
    val timestamp: Long
)
