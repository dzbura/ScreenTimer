package com.example.screentimer.ui

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.screentimer.R
import com.example.screentimer.UsageStatsService
import com.example.screentimer.databinding.FragmentTodayBinding


class TodayFragment : Fragment() {

    private val sharedViewModel : MainViewModel by activityViewModels()

    private var binding : FragmentTodayBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (UsageStatsService.checkUsageStatsPermission(requireContext())) {
            sharedViewModel._stToday.value = UsageStatsService.getUsageStats(requireContext())
        } else {
            startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS))
        }
        val fragmentBinding = FragmentTodayBinding.inflate(inflater,container, false)
        binding = fragmentBinding

        if (sharedViewModel.stGoal.value == null) {
            fragmentBinding.textView.text = getString(R.string.phone_usage_today, formatTime(sharedViewModel.stToday.value!!))
            fragmentBinding.textView1.text = getString(R.string.set_daily_goal_info)
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
            fragmentBinding.gauge1.pointSize = 360
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


    fun setGoal() {
        findNavController().navigate(R.id.action_todayFragment_to_setGoalFragment)
    }

    fun formatTime(minutes : Int) : String {
        var formattedTime = ""
        if (minutes < 60) {
            formattedTime = getString(R.string.minutes_time_format, minutes)
        } else {
            formattedTime = if (minutes.rem(60) > 0)  getString(R.string.hours_time_format, minutes / 60) + getString(R.string.minutes_time_format, minutes.rem(60)) else getString(R.string.hours_time_format, minutes / 60)
        }
        return formattedTime
    }
}