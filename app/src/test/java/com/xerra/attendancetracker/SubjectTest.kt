package com.xerra.attendancetracker

import com.xerra.attendancetracker.domain.model.Subject
import org.junit.Assert.assertEquals
import org.junit.Test

class SubjectTest {

    @Test
    fun testPercentageCalculation() {
        val subject = Subject(
            id = 1,
            name = "Test",
            teacherName = null,
            color = 0,
            presentCount = 15,
            totalCount = 20,
            createdDate = 0,
            updatedDate = 0
        )
        assertEquals(75.0, subject.percentage, 0.01)
        assertEquals("75.0%", subject.formattedPercentage)
    }

    @Test
    fun testPercentageZeroClasses() {
        val subject = Subject(
            id = 1,
            name = "Test",
            teacherName = null,
            color = 0,
            presentCount = 0,
            totalCount = 0,
            createdDate = 0,
            updatedDate = 0
        )
        assertEquals(0.0, subject.percentage, 0.01)
        assertEquals("0.0%", subject.formattedPercentage)
    }

    @Test
    fun testGoalStatusBelowTarget() {
        val subject = Subject(
            id = 1,
            name = "Test",
            teacherName = null,
            color = 0,
            presentCount = 6,
            totalCount = 10,
            createdDate = 0,
            updatedDate = 0
        )
        // Target is 75%, current is 60%.
        // x >= (0.75*10 - 6) / (1 - 0.75) = (7.5 - 6) / 0.25 = 1.5 / 0.25 = 6 classes
        val status = subject.getGoalStatus(75.0)
        assertEquals("You need to attend the next 6 classes.", status)
    }

    @Test
    fun testGoalStatusAboveTarget() {
        val subject = Subject(
            id = 1,
            name = "Test",
            teacherName = null,
            color = 0,
            presentCount = 9,
            totalCount = 10,
            createdDate = 0,
            updatedDate = 0
        )
        // Target is 75%, current is 90%.
        // y <= (9 - 0.75*10) / 0.75 = (9 - 7.5) / 0.75 = 1.5 / 0.75 = 2 classes
        val status = subject.getGoalStatus(75.0)
        assertEquals("On track! You can skip the next 2 classes.", status)
    }
}
