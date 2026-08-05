package com.xerra.attendancetracker.domain.model

import kotlin.math.ceil
import kotlin.math.floor

data class Subject(
    val id: Long = 0,
    val name: String,
    val teacherName: String?,
    val color: Int,
    val presentCount: Int,
    val totalCount: Int,
    val createdDate: Long,
    val updatedDate: Long
) {
    val percentage: Double
        get() = if (totalCount == 0) 0.0 else (presentCount.toDouble() / totalCount.toDouble() * 100.0)

    val formattedPercentage: String
        get() = String.format("%.1f%%", percentage)

    fun getGoalStatus(targetPercentage: Double): String {
        if (targetPercentage <= 0.0) return "No target set"
        val target = targetPercentage / 100.0

        if (targetPercentage >= 100.0) {
            return if (presentCount == totalCount) {
                "Perfect! Attend all classes to maintain 100%."
            } else {
                "Cannot reach 100% attendance due to past absences."
            }
        }

        val p = presentCount
        val t = totalCount

        if (t == 0) {
            return "No classes recorded yet."
        }

        val currentPct = p.toDouble() / t.toDouble()

        return if (currentPct >= target) {
            val maxSkip = floor((p - target * t) / target).toInt()
            if (maxSkip > 0) {
                "On track! You can skip the next $maxSkip class${if (maxSkip > 1) "es" else ""}."
            } else {
                "On track! You cannot skip any more classes."
            }
        } else {
            val needed = ceil((target * t - p) / (1.0 - target)).toInt()
            "You need to attend the next $needed class${if (needed > 1) "es" else ""}."
        }
    }
}
