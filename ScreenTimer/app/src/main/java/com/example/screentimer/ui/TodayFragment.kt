package com.example.screentimer.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.screentimer.R
import com.example.screentimer.UsageStatsService
import com.example.screentimer.data.DayStat
import com.example.screentimer.data.Stat
import com.example.screentimer.databinding.FragmentTodayBinding
import com.example.screentimer.ui.adapters.StatAdapter
import com.example.screentimer.ui.adapters.StatItemClickListener


class TodayFragment : Fragment(), StatItemClickListener {

    companion object {
        val TAG = "Stat"
    }

    lateinit var mAdapter: StatAdapter
    private val sharedViewModel : MainViewModel by activityViewModels()
    private var binding : FragmentTodayBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val fragmentBinding = FragmentTodayBinding.inflate(inflater,container, false)
        mAdapter = StatAdapter()

        if (UsageStatsService.checkUsageStatsPermission(requireContext())) {
            val stat : DayStat = UsageStatsService.getTodayStats(context)
            sharedViewModel._stToday.value = (stat.toalTime/60000).toInt()
            val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE)
            val dailyGoal = sharedPref?.getInt(getString(R.string.preference_daily_goal), 0)
            when {
                dailyGoal == 0 -> {
                    fragmentBinding.currentUsageInfo.text = getString(R.string.phone_usage_today_nogoal, formatTime(sharedViewModel.stToday.value!!))
                    fragmentBinding.currentUsageInfo2.text = getString(R.string.set_daily_goal_info)
                    fragmentBinding.currentUsageGauge.endValue = sharedViewModel.stToday.value!!*2
                    fragmentBinding.currentUsageGauge.value = sharedViewModel.stToday.value!!
                }
                sharedViewModel.stToday.value!! < dailyGoal!! -> {
                    fragmentBinding.currentUsageInfo.text = getString(R.string.phone_usage_today, formatTime(sharedViewModel.stToday.value!!), formatTime(dailyGoal))
                    fragmentBinding.currentUsageInfo2.text = getString(R.string.remaining_screentime, formatTime(dailyGoal - sharedViewModel.stToday.value!!))
                    fragmentBinding.currentUsageGauge.endValue = dailyGoal
                    fragmentBinding.currentUsageGauge.value = sharedViewModel.stToday.value!!
                }
                sharedViewModel.stToday.value!! > dailyGoal -> {
                    fragmentBinding.currentUsageInfo.text =  getString(R.string.time_exceeded_info, formatTime(sharedViewModel.stToday.value!!), formatTime(dailyGoal), formatTime(sharedViewModel.stToday.value!! - dailyGoal))
                    fragmentBinding.currentUsageInfo2.text = getString(R.string.reassurance)
                    fragmentBinding.currentUsageGauge.pointSize = 360
                }
            }
            binding = fragmentBinding

            mAdapter.submitList(stat.stats)
            binding!!.recyclerView.adapter = mAdapter
        } else {
            startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS))
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

    fun formatTime(minutes : Int) : String {
        return if (minutes < 60) {
            getString(R.string.minutes_time_format, minutes)
        } else {
            if (minutes.rem(60) > 0)  getString(R.string.hours_time_format, minutes / 60) + " " + getString(R.string.minutes_time_format, minutes.rem(60)) else getString(R.string.hours_time_format, minutes / 60)
        }
    }

    override fun chooseStat(stat: Stat) {
        TODO("Not yet implemented")
    }
}