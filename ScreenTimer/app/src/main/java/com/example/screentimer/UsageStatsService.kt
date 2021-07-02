package com.example.screentimer

import android.app.AppOpsManager
import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.Context
import android.os.Build
import com.example.screentimer.data.DayStat
import com.example.screentimer.data.Stat
import com.example.screentimer.data.WeekStat
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.util.*

object UsageStatsService {

    fun getStats(ctx: Context?, start:Long, end:Long) :  MutableMap<String, MutableList<UsageEvents.Event>> {
        //TODO: Could I make this generic?

        var usageManager: UsageStatsManager = ctx?.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
        val sortedEvents = mutableMapOf<String, MutableList<UsageEvents.Event>>()
        val systemEvents = usageManager.queryEvents(start, end)
        while (systemEvents.hasNextEvent()) {
            val event = UsageEvents.Event()
            systemEvents.getNextEvent(event)
            val packageEvents = sortedEvents[event.packageName] ?: mutableListOf()
            packageEvents.add(event)
            sortedEvents[event.packageName] = packageEvents
        }
        return sortedEvents
    }

    fun getStatsForWeek(ctx: Context?, start:Long, end:Long) :  MutableMap<LocalDate, MutableMap<String, MutableList<UsageEvents.Event>>> {
        var usageManager: UsageStatsManager = ctx?.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
        val sortedEvents = mutableMapOf<LocalDate, MutableMap<String, MutableList<UsageEvents.Event>>>()
        val systemEvents = usageManager.queryEvents(start, end)
        while (systemEvents.hasNextEvent()) {
            val event = UsageEvents.Event()
            systemEvents.getNextEvent(event)
            val eventTimestampDate = getLocalDateFromLong(event.timeStamp)
            if (!sortedEvents.containsKey(eventTimestampDate)) {
                sortedEvents[eventTimestampDate] = mutableMapOf<String,MutableList<UsageEvents.Event>>()
            }
            if (!sortedEvents[eventTimestampDate]!!.containsKey(event.packageName)) {
                sortedEvents[eventTimestampDate]!![event.packageName] = mutableListOf<UsageEvents.Event>()
            }
            sortedEvents[eventTimestampDate]!![event.packageName]!!.add(event)
        }
        return sortedEvents
    }

    fun getLocalDateFromLong(time: Long) : LocalDate {
        return Instant.ofEpochMilli(time).atZone(ZoneId.systemDefault()).toLocalDate();
    }

    fun getWeeksStats(ctx: Context?, offset: Long) : WeekStat {
        val utc = ZoneId.of("UTC")
        val defaultZone = ZoneId.systemDefault()
        val startDate = LocalDate.now().minusDays(7*offset).atStartOfDay(defaultZone).withZoneSameInstant(utc)
        val start = startDate.toInstant().toEpochMilli()
        val end = startDate.plusDays(8).toInstant().toEpochMilli()
        val weekStat = WeekStat(getLocalDateFromLong(start), getLocalDateFromLong(end), arrayListOf<DayStat>())
        getStatsForWeek(ctx, start, end).forEach(){date, eventsByPkg ->
            val pkgsToSkip: Array<String> = ctx?.resources?.getStringArray(R.array.excluded_apps) as Array<String>
            var statDay = DayStat(arrayListOf<Stat>(), date, formatDate(date),0, "")
            eventsByPkg.forEach{packageName, events ->
                if (!pkgsToSkip.contains(packageName)) {
                    var startTime = 0L
                    var endTime = 0L
                    var totalTime = 0L
                    val startDay = date.atStartOfDay(defaultZone).toInstant()?.toEpochMilli()
                    val endDay = date.atTime(LocalTime.MAX).atZone(defaultZone).toInstant().toEpochMilli()
                    events.forEach {
                        if (it.eventType == UsageEvents.Event.MOVE_TO_FOREGROUND) {
                            startTime = it.timeStamp
                        } else if (it.eventType == UsageEvents.Event.MOVE_TO_BACKGROUND) {
                            endTime = it.timeStamp
                        }
                        if (startTime == 0L && endTime != 0L) {
                            startTime = startDay!!
                        }
                        if (startTime != 0L && endTime != 0L) {
                            totalTime += endTime - startTime
                            startTime = 0L
                            endTime = 0L
                        }
                    }
                    if (startTime != 0L && endTime == 0L) {
                        totalTime += endDay - 1000 - startTime
                    }
                    statDay.stats.add(Stat(packageName.substringAfterLast("."),totalTime, formatTime(totalTime, ctx)))
                    statDay.toalTime += totalTime

                }
            }
            statDay.totalTimeString = ctx!!.getString(R.string.day_item_total_usage, formatTime(statDay.toalTime, ctx))
            weekStat.stats.add(statDay)
        }
        return weekStat
    }

    fun getTodayStats(ctx: Context?) : DayStat {
        val utc = ZoneId.of("UTC")
        val defaultZone = ZoneId.systemDefault()
        val startDate = LocalDate.now().atStartOfDay(defaultZone).withZoneSameInstant(utc)
        val start = startDate.toInstant().toEpochMilli()
        val end = startDate.plusDays(1).toInstant().toEpochMilli()
        val stats = arrayListOf<Stat>()
        var grandTotal = 0L

        // Go through the events by package name
        getStats(ctx, start, end).forEach { packageName, events ->
            val pkgsToSkip: Array<String> = ctx?.resources?.getStringArray(R.array.excluded_apps) as Array<String>
            if (!pkgsToSkip.contains(packageName)){
                var startTime = 0L
                var endTime = 0L
                var totalTime = 0L
                events.forEach {
                    if (it.eventType == UsageEvents.Event.MOVE_TO_FOREGROUND) {
                        startTime = it.timeStamp
                    } else if (it.eventType == UsageEvents.Event.MOVE_TO_BACKGROUND) {
                        endTime = it.timeStamp
                    }
                    //  The app was started on the previous day
                    if (startTime == 0L && endTime != 0L) {
                        startTime = start
                    }
                    //  Usual session
                    if (startTime != 0L && endTime != 0L) {
                        totalTime += endTime - startTime
                        startTime = 0L
                        endTime = 0L
                    }
                }
                //  the app was used past midnight
                if (startTime != 0L && endTime == 0L && packageName != "com.example.screentimer") {
                    totalTime += end - 1000 - startTime
                }
                stats.add(
                    Stat(
                        packageName.substringAfterLast("."),
                        totalTime,
                        ctx.getString(R.string.day_item_total_usage, formatTime(totalTime, ctx))
                    )
                )
                grandTotal = grandTotal + totalTime
            }
        }
        return DayStat(
            stats,
            LocalDate.now(),
            formatDate(LocalDate.now()),
            grandTotal,
            ctx!!.getString(R.string.day_item_total_usage, formatTime(grandTotal/60000, ctx))
        )
    }

    fun checkUsageStatsPermission(ctx : Context): Boolean {
        //TODO: fix permission check
        var appOpsManager: AppOpsManager? = null
        var mode: Int = 0
        appOpsManager = ctx?.getSystemService(Context.APP_OPS_SERVICE)!! as AppOpsManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            mode = appOpsManager.unsafeCheckOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, 1, ctx.opPackageName)
        }
        return true//mode == AppOpsManager.MODE_ALLOWED
    }

    fun getMonthStats(ctx: Context?) : ArrayList<WeekStat> {
        //TODO: implement for month
        return arrayListOf<WeekStat>()
    }
}