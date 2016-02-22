package com.widget.tools

import org.junit.Assert
import org.junit.Test

class TestUtilsTest {

    @Test
    fun dateUtilsShouldProperlyFormatDate() {
        val dateTime = 1451606400000;
        Assert.assertEquals("01/01/2016", convertToDateFormat(dateTime))
    }

    @Test
    fun dateUtilsShouldProperlyFormatDateWithTime() {
        val dateTime = 1451606400000;
        Assert.assertEquals("01/01/2016 02:00", convertToDateTimeFormat(dateTime))
    }
}