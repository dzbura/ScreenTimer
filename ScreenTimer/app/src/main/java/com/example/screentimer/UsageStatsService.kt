package com.example.screentimer

import android.app.AppOpsManager
import android.app.usage.UsageStats
import android.app.usage.UsageStatsManager
import android.content.Context
import android.os.Build
import android.os.Process
import android.util.Log
import java.util.*

object UsageStatsService {

    fun getUsageStats(ctx : Context) : Int {
        var usageStatsManager: UsageStatsManager = ctx.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
        var cal: Calendar = Calendar.getInstance()
        cal.add(Calendar.DAY_OF_MONTH, -1)
        var queryUsageStats : MutableList<UsageStats> = usageStatsManager.queryUsageStats(
            UsageStatsManager.INTERVAL_DAILY, cal.timeInMillis, System.currentTimeMillis())
        var totalToday : Long = 0L
        queryUsageStats.forEach {
            Log.d("foreach",it.packageName + it.totalTimeInForeground)
            totalToday += it.totalTimeInForeground
        }
        return (totalToday/60000).toInt()
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
}