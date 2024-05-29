package com.ccs.fish.scan.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun convertDateFormat(inputDate: String): String {
    val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val outputFormat = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
    val parsedDate = inputFormat.parse(inputDate)
    return outputFormat.format(parsedDate)
}

fun convertTimestamp(timestamp: Long): String {
    val date = Date(timestamp)
    val format = SimpleDateFormat("dd MMMM yyyy h:mm a", Locale.getDefault())
    return format.format(date)
}