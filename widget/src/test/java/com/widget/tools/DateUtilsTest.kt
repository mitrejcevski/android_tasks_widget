package com.widget.tools

import org.junit.Assert.assertEquals
import org.junit.Test

class DateUtilsTest {

    @Test
    fun dateUtilsShouldProperlyFormatDate() {
        val dateTime = 1451606400000;
        assertEquals("01/01/2016", convertToDateFormat(dateTime))
    }

    @Test
    fun dateUtilsShouldProperlyFormatDateWithTime() {
        val dateTime = 1451600000000;
        assertEquals("01/01/2016 00:13", convertToDateTimeFormat(dateTime))
    }
}