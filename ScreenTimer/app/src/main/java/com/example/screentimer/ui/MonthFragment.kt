package com.example.screentimer.ui

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.screentimer.UsageStatsService
import com.example.screentimer.data.WeekStat
import com.example.screentimer.databinding.FragmentMonthBinding
import com.example.screentimer.ui.adapters.WeekStatAdapter
import com.example.screentimer.ui.adapters.WeekStatItemClickListener


class MonthFragment : Fragment(), WeekStatItemClickListener {


    companion object {
        val TAG = "WeekStat"
    }

    lateinit var mAdapter: WeekStatAdapter
    private val sharedViewModel : MainViewModel by activityViewModels()
    private var binding : FragmentMonthBinding? = null

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val fragmentBinding = FragmentMonthBinding.inflate(inflater,container,false)
        mAdapter = WeekStatAdapter(this)
        var stats = arrayListOf<WeekStat>()
        if (UsageStatsService.checkUsageStatsPermission(requireContext())) {
            stats = UsageStatsService.getMonthStats(context)
        }
        binding = fragmentBinding
        mAdapter.submitList(stats)
        binding!!.recyclerView.adapter = mAdapter
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

    override fun chooseWeekStat(weekStat: WeekStat) {
        TODO("Not yet implemented")
    }
}