package com.dmitry.yume.domain.format

import org.junit.Assert.assertEquals
import org.junit.Test

class TimeFormattingTest {
    @Test
    fun `negative player time is formatted as zero`() {
        assertEquals("00:00", formatTime(Long.MIN_VALUE))
        assertEquals("00:00", formatTime(-1L))
    }
}
