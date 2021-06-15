package com.example.screentimer

import android.app.AppOpsManager
import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.Context
import android.os.Build
import android.os.Process
import androidx.annotation.RequiresApi
import java.time.LocalDate
import java.time.ZoneId

object UsageStatsService {

    fun getStats(ctx: Context?, start:Long, end:Long) :  MutableMap<String, MutableList<UsageEvents.Event>> {
        var usageManager: UsageStatsManager = ctx?.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
        val sortedEvents = mutableMapOf<String, MutableList<UsageEvents.Event>>()
        val systemEvents = usageManager.queryEvents(start, end)
        while (systemEvents.hasNextEvent()) {
            val event = UsageEvents.Event()
            systemEvents.getNextEvent(event)

            // Get the list of events for the package name, create one if it doesn't exist
            val packageEvents = sortedEvents[event.packageName] ?: mutableListOf()
            packageEvents.add(event)
            sortedEvents[event.packageName] = packageEvents
        }
        return sortedEvents
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
        var totalTime = 0L
        statList.forEach() {
            totalTime += it.totalTime
        }
        return totalTime
    }

    fun checkUsageStatsPermission(ctx : Context): Boolean {
        var appOpsManager: AppOpsManager? = null
        var mode: Int = 0
        appOpsManager = ctx?.getSystemService(Context.APP_OPS_SERVICE)!! as AppOpsManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            mode = appOpsManager.unsafeCheckOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, Process.myUid(), ctx.opPackageName)
        }
        return mode == AppOpsManager.MODE_ALLOWED
    }

    class Stat(val packageName: String, val totalTime: Long)
}