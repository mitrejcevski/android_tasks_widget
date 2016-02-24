package com.widget.tools

import org.junit.Assert.assertEquals
import org.junit.Test

class DateUtilsTest {

    @Test
    fun dateUtilsShouldProperlyFormatDate() {
        val dateTime = 1451606400000;
        assertEquals("01/01/2016", convertToDateFormat(dateTime))
    }
}