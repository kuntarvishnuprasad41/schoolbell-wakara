package com.app.whakaara.utils

import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.Calendar

class DateUtilsTest {
    @Test
    fun `convert seconds to time until alarm string`() {
        // Given
        val time: Long = 111222333

        // When
        val timeString = DateUtils.convertSecondsToHMm(time)

        // Then
        assertEquals("Alarm in 7 hours 5 minutes", timeString)
    }

    @Test
    fun `convert time to 24 hour format`() {
        // Given
        val date = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 12)
            set(Calendar.MINUTE, 34)
            set(Calendar.SECOND, 0)
        }

        // When
        val time24HourFormat = DateUtils.alarmTimeTo24HourFormat(date = date)

        // Then
        assertEquals("12:34 PM", time24HourFormat)
    }

    @Test
    fun `get initial time to alarm when alarm disabled`() {
        // Given + When
        val initial = DateUtils.getInitialTimeToAlarm(false, Calendar.getInstance())

        // Then
        assertEquals("Off", initial)
    }
}
