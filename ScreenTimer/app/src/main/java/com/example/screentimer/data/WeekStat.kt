package com.example.screentimer.data

import java.time.LocalDate

class WeekStat(
    var startDate: LocalDate,
    var endDate: LocalDate,
    var stats: ArrayList<DayStat>,
    var dailyMeanString: String,
    var dailyMeanMillis: Long
) {
}