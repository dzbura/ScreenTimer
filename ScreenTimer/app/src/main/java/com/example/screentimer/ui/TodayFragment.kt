package com.example.screentimer.ui

import android.app.AppOpsManager
import android.app.usage.UsageStats
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Process
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.screentimer.R
import com.example.screentimer.databinding.FragmentTodayBinding
import java.util.*


class TodayFragment : Fragment() {

    private val sharedViewModel : MainViewModel by activityViewModels()

    private var binding : FragmentTodayBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (checkUsageStatsPermission()) {
            sharedViewModel._stToday.value = getUsageStats()
        } else {
            startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS))
        }
        val fragmentBinding = FragmentTodayBinding.inflate(inflater,container, false)
        binding = fragmentBinding

        if (sharedViewModel.stGoal.value == null) {
            fragmentBinding.textView.text = "You have used your phone foe " + sharedViewModel.stToday.value.toString() + " minutes today so far"
            fragmentBinding.textView1.text = "Set your daily goal by clicking \"Set goal\""
            fragmentBinding.gauge1.endValue = sharedViewModel.stToday.value!!*2
            fragmentBinding.gauge1.value = sharedViewModel.stToday.value!!
        } else if (sharedViewModel.stToday.value!! < sharedViewModel.stGoal.value!!) {
            fragmentBinding.textView.text = "You have used yout phone for " + sharedViewModel.stToday.value.toString() + " minutes out of "  + sharedViewModel.stGoal.value.toString() + " set as a daily goal."
            fragmentBinding.textView1.text = "Remaining time until you exceed your daily screentime goal is " + (sharedViewModel.stGoal.value!! - sharedViewModel.stToday.value!!).toString() + " minutes. Use it wisely!"
            fragmentBinding.gauge1.endValue = sharedViewModel.stGoal.value!!
            fragmentBinding.gauge1.value = sharedViewModel.stToday.value!!
        } else if (sharedViewModel.stToday.value!! > sharedViewModel.stGoal.value!!) {
            fragmentBinding.textView.text = "You have used yout phone for " + sharedViewModel.stToday.value.toString() + " today and exceeded your goal of "  + sharedViewModel.stGoal.value.toString() + " by " + (sharedViewModel.stToday.value!! - sharedViewModel.stGoal.value!!).toString() + " minutes."
            fragmentBinding.textView1.text = "I hope you do better tomorrow!"
            fragmentBinding.gauge1.endValue = 1
            fragmentBinding.gauge1.pointSize = 1000
        }
        return fragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = sharedViewModel
            mainFragment = this@TodayFragment
        }
    }

    private fun checkUsageStatsPermission(): Boolean {
        var appOpsManager: AppOpsManager? = null
        var mode: Int = 0
        appOpsManager = context?.getSystemService(Context.APP_OPS_SERVICE)!! as AppOpsManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            mode = appOpsManager.unsafeCheckOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, Process.myUid() , requireActivity().getPackageName())
        }
        return mode == AppOpsManager.MODE_ALLOWED
    }

    fun setGoal() {
        findNavController().navigate(R.id.action_todayFragment_to_setGoalFragment)
    }

    fun getUsageStats() : Int {
        var usageStatsManager: UsageStatsManager = context?.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
        var cal: Calendar = Calendar.getInstance()
        cal.add(Calendar.DAY_OF_MONTH, -1)
        var queryUsageStats : MutableList<UsageStats> = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, cal.timeInMillis,
            System.currentTimeMillis())
        var totalToday : Long = 0L
        queryUsageStats.forEach {
            Log.d("foreach",it.packageName + it.totalTimeInForeground)
                totalToday += it.totalTimeInForeground
            }
        return (totalToday/60000).toInt()
    }
}