package com.xerra.attendancetracker.data.mapper

import com.xerra.attendancetracker.data.database.entity.AttendanceHistoryEntity
import com.xerra.attendancetracker.data.database.entity.SubjectEntity
import com.xerra.attendancetracker.domain.model.AttendanceHistory
import com.xerra.attendancetracker.domain.model.Subject

fun SubjectEntity.toDomain(): Subject = Subject(
    id = id,
    name = name,
    teacherName = teacherName,
    color = color,
    presentCount = presentCount,
    totalCount = totalCount,
    createdDate = createdDate,
    updatedDate = updatedDate
)

fun Subject.toEntity(): SubjectEntity = SubjectEntity(
    id = id,
    name = name,
    teacherName = teacherName,
    color = color,
    presentCount = presentCount,
    totalCount = totalCount,
    createdDate = createdDate,
    updatedDate = updatedDate
)

fun AttendanceHistoryEntity.toDomain(): AttendanceHistory = AttendanceHistory(
    id = id,
    subjectId = subjectId,
    subjectName = subjectName,
    status = status,
    timestamp = timestamp
)

fun AttendanceHistory.toEntity(): AttendanceHistoryEntity = AttendanceHistoryEntity(
    id = id,
    subjectId = subjectId,
    subjectName = subjectName,
    status = status,
    timestamp = timestamp
)
