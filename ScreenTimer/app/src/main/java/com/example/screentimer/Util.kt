package com.example.screentimer

import android.content.Context
import java.time.LocalDate
import java.time.format.DateTimeFormatter

fun formatTime(minutes : Int, ctx : Context) : String {
    var formattedTime = ""
    if (minutes < 60) {
        formattedTime = ctx.getString(R.string.minutes_time_format, minutes)
    } else {
        formattedTime = if (minutes.rem(60) > 0)  ctx.getString(R.string.hours_time_format, minutes / 60) + " " + ctx.getString(R.string.minutes_time_format, minutes.rem(60)) else ctx.getString(R.string.hours_time_format, minutes / 60)
    }
    return formattedTime
}

fun formatTime(minutes : Long, ctx : Context) : String {
    return formatTime(minutes.toInt(), ctx)
}

fun formatDate(date: LocalDate): String {
    val format = DateTimeFormatter.ofPattern("EEE MM.dd ")
    return format.format(date)
}

