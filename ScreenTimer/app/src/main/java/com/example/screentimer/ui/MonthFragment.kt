package com.example.screentimer.ui

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.screentimer.UsageStatsService
import com.example.screentimer.databinding.FragmentMonthBinding


class MonthFragment : Fragment() {

    private val sharedViewModel : MainViewModel by activityViewModels()
    private var binding : FragmentMonthBinding? = null

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val fragmentBinding = FragmentMonthBinding.inflate(inflater,container,false)
        if (UsageStatsService.checkUsageStatsPermission(requireContext())) {
            var statList: Map<String, Map<String, UsageStatsService.Stat>> = UsageStatsService.getMonthStats(context)
            statList.forEach() {date, statsForPackage ->
                statsForPackage.forEach() {packageName, stat ->
                    Log.d(" tescik","entry for " + date + " for package " + packageName + " totalTime " + stat.totalTime/60000)
                }
            }
        }
        binding = fragmentBinding
        return fragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.apply{
            lifecycleOwner = viewLifecycleOwner
            viewModel = sharedViewModel
            mainFragment = this@MonthFragment
        }
    }
}