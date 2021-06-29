package com.example.screentimer.data

import java.time.LocalDate

class DayStat (
    var stats: ArrayList<Stat>,
    var date: LocalDate,
    var dateString: String,
    var toalTime: Long) {
}