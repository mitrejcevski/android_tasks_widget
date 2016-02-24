package com.widget.tools

import java.text.SimpleDateFormat
import java.util.*

fun convertToDateFormat(dateTime: Long): String {
    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.US)
    return dateFormat.format(dateTime)
}