package com.example.screentimer.data

import java.time.LocalDate

class DayStat (
    var stats: ArrayList<Stat>,
    var date: LocalDate,
    var dateString: String,
    var toalTime: Long,
    var totalTimeString :String//Resources.getSystem().getString(R.string.day_item_total_usage, formatTime(toalTime/60000))
) {
}