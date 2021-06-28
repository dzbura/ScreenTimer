package com.example.screentimer

import android.app.AppOpsManager
import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.ZoneId
import java.util.*

object UsageStatsService {

    fun getStats(ctx: Context?, start:Long, end:Long) :  MutableMap<String, MutableList<UsageEvents.Event>> {
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

    fun getStatsForWeek(ctx: Context?, start:Long, end:Long) :  MutableMap<String, MutableMap<String, MutableList<UsageEvents.Event>>> {
        var usageManager: UsageStatsManager = ctx?.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
        val sortedEvents = mutableMapOf<String, MutableMap<String, MutableList<UsageEvents.Event>>>()
        val systemEvents = usageManager.queryEvents(start, end)
        while (systemEvents.hasNextEvent()) {
            val event = UsageEvents.Event()
            systemEvents.getNextEvent(event)
            if (!sortedEvents.containsKey(convertLongToTime(event.timeStamp))) {
                sortedEvents[convertLongToTime(event.timeStamp)] = mutableMapOf<String,MutableList<UsageEvents.Event>>()
            }
            if (!sortedEvents[convertLongToTime(event.timeStamp)]!!.containsKey(event.packageName)) {
                sortedEvents[convertLongToTime(event.timeStamp)]!![event.packageName] = mutableListOf<UsageEvents.Event>()
            }
            sortedEvents[convertLongToTime(event.timeStamp)]!![event.packageName]!!.add(event)
        }
        return sortedEvents
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getWeeksStats(ctx: Context?) : Map<String, Map<String, Stat>> {
        val utc = ZoneId.of("UTC")
        val defaultZone = ZoneId.systemDefault()
        val startDate = LocalDate.now().minusDays(7).atStartOfDay(defaultZone).withZoneSameInstant(utc) //TODO:other weeks
        val start = startDate.toInstant().toEpochMilli()
        val end = startDate.plusDays(7).toInstant().toEpochMilli()
        val statsMap = mutableMapOf<String, MutableMap<String, Stat>>()

        getStatsForWeek(ctx, start, end).forEach(){date, eventsByPkg ->
            val stats = mutableListOf<Stat>()
            val pkgsToSkip: Array<String> = ctx?.resources?.getStringArray(R.array.excluded_apps) as Array<String>
            eventsByPkg.forEach{packageName, events ->
                if (!pkgsToSkip.contains(packageName)) {
                    var startTime = 0L
                    var endTime = 0L
                    var totalTime = 0L
                    events.forEach {
                        if (it.eventType == UsageEvents.Event.MOVE_TO_FOREGROUND) {
                            startTime = it.timeStamp
                        } else if (it.eventType == UsageEvents.Event.MOVE_TO_BACKGROUND) {
                            endTime = it.timeStamp
                        }
                        if (startTime == 0L && endTime != 0L) {
                            startTime = start
                        }
                        if (startTime != 0L && endTime != 0L) {
                            totalTime += endTime - startTime
                            startTime = 0L
                            endTime = 0L
                        }
                    }
                    if (startTime != 0L && endTime == 0L) {
                        totalTime += end - 1000 - startTime
                    }
                    if (!statsMap.containsKey(date)) {
                        statsMap[date] = mutableMapOf<String, Stat>()
                    }
                    statsMap[date]!![packageName] =  Stat(packageName, totalTime)
                }
            }
        }
        return statsMap
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getTodayStats(ctx: Context?) : List<Stat> {
        val utc = ZoneId.of("UTC")
        val defaultZone = ZoneId.systemDefault()
        val startDate = LocalDate.now().atStartOfDay(defaultZone).withZoneSameInstant(utc)
        val start = startDate.toInstant().toEpochMilli()
        val end = startDate.plusDays(1).toInstant().toEpochMilli()
        val stats = mutableListOf<Stat>()

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
                stats.add(Stat(packageName, totalTime))
            }
        }
        return stats
    }

    fun getTotals(statLists : Map<LocalDate, List<Stat>>) : Map<LocalDate, Long> {
        var dateToTotal : MutableMap<LocalDate, Long> = mutableMapOf<LocalDate, Long>()
        statLists.keys.forEach(){
            var currentTotal = 0L
            statLists[it]?.forEach(){
                currentTotal += it.totalTime
            }
            dateToTotal[it] = currentTotal
        }
        return dateToTotal

    }

    fun getTotal(statList : List<Stat>) : Long {
        //TODO: make generic
        var totalTime = 0L
        statList.forEach() {
            totalTime += it.totalTime
        }
        return totalTime
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

    fun convertLongToTime(time: Long): String {
        val date = Date(time)
        val format = SimpleDateFormat("yyyy.MM.dd")
        return format.format(date)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getMonthStats(ctx: Context?) : Map<String, Map<String, Stat>> {
        //TODO: implement for month (this is mock copied from week)
        //TODO: also make this generic with date parameter passed as arg
        val utc = ZoneId.of("UTC")
        val defaultZone = ZoneId.systemDefault()
        val startDate = LocalDate.now().minusDays(7).atStartOfDay(defaultZone).withZoneSameInstant(utc) //TODO:other weeks
        val start = startDate.toInstant().toEpochMilli()
        val end = startDate.plusDays(7).toInstant().toEpochMilli()
        val statsMap = mutableMapOf<String, MutableMap<String, Stat>>()

        getStatsForWeek(ctx, start, end).forEach(){date, eventsByPkg ->
            val stats = mutableListOf<Stat>()
            val pkgsToSkip: Array<String> = ctx?.resources?.getStringArray(R.array.excluded_apps) as Array<String>
            eventsByPkg.forEach{packageName, events ->
                if (!pkgsToSkip.contains(packageName)) {
                    var startTime = 0L
                    var endTime = 0L
                    var totalTime = 0L
                    events.forEach {
                        if (it.eventType == UsageEvents.Event.MOVE_TO_FOREGROUND) {
                            startTime = it.timeStamp
                        } else if (it.eventType == UsageEvents.Event.MOVE_TO_BACKGROUND) {
                            endTime = it.timeStamp
                        }
                        if (startTime == 0L && endTime != 0L) {
                            startTime = start
                        }
                        if (startTime != 0L && endTime != 0L) {
                            totalTime += endTime - startTime
                            startTime = 0L
                            endTime = 0L
                        }
                    }
                    if (startTime != 0L && endTime == 0L && packageName != "com.example.screentimer") {
                        totalTime += end - 1000 - startTime
                    }
                    if (!statsMap.containsKey(date)) { //variable??
                        statsMap[date] = mutableMapOf<String, Stat>()
                    }

                    statsMap[date]!![packageName] =  Stat(packageName, totalTime)
                }
            }
        }
        return statsMap
    }


    class Stat(val packageName: String, val totalTime: Long)
}