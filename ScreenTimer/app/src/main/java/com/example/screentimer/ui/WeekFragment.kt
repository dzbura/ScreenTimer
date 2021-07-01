package com.example.screentimer.ui

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
import com.example.screentimer.data.WeekStat
import com.example.screentimer.databinding.FragmentWeekBinding
import com.example.screentimer.formatDate
import com.example.screentimer.ui.adapters.DayStatAdapter
import com.example.screentimer.ui.adapters.DayStatItemClickListener
import com.example.screentimer.ui.adapters.StatItemClickListener
import java.time.LocalDate

class WeekFragment : Fragment(), DayStatItemClickListener, StatItemClickListener {

    companion object {
        val TAG = "DayStat"
    }

    lateinit var mAdapter: DayStatAdapter
    //lateinit var mDetAdapter: StatAdapter

    private val sharedViewModel : MainViewModel by activityViewModels()
    private var binding : FragmentWeekBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val fragmentBinding = FragmentWeekBinding.inflate(inflater,container,false)
        mAdapter = DayStatAdapter(this)
        //mDetAdapter = StatAdapter()
        var stats: WeekStat = WeekStat(LocalDate.now(), LocalDate.now().minusDays(7), arrayListOf<DayStat>())
        if (UsageStatsService.checkUsageStatsPermission(requireContext())) {
            stats = UsageStatsService.getWeeksStats(context, sharedViewModel.weekOffset.value!!)
        } else {
            startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS))
        }
        sharedViewModel._displayedWeekString.value = getString(R.string.week_to_display, formatDate(stats.startDate), formatDate(stats.endDate))
        binding = fragmentBinding
/*
        stats.stats.forEach() {
            mDetAdapter.submitList(it.stats)
            binding!!.recyclerView[]
        }
*/
        mAdapter.submitList(stats.stats)
        binding!!.recyclerView.adapter = mAdapter
        return fragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.apply{
            lifecycleOwner = viewLifecycleOwner
            viewModel = sharedViewModel
            mainFragment = this@WeekFragment
        }
    }

    override fun chooseDayStat(dayStat: DayStat) {
        TODO("Not yet implemented")
    }

    fun navigateWeeks(offset : Long) {
        if (offset >= 0L || sharedViewModel._weekOffset.value!! >= 0L) {
            var stats: WeekStat = WeekStat(LocalDate.now(), LocalDate.now().minusDays(7), arrayListOf<DayStat>())
            if (offset == 0L) {
                sharedViewModel._weekOffset.value = 1L
            } else {
                sharedViewModel._weekOffset.value = sharedViewModel._weekOffset.value?.plus(offset)
            }
            if (UsageStatsService.checkUsageStatsPermission(requireContext())) {
                stats = UsageStatsService.getWeeksStats(context, sharedViewModel._weekOffset.value!!)
            } else {
                startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS))
            }
            if (stats.stats.isEmpty()) {
                sharedViewModel._noData.value = getString(R.string.no_data_for_week)
            } else {
                sharedViewModel._noData.value = ""
            }
            mAdapter.submitList(stats.stats)
            sharedViewModel._displayedWeekString.value = getString(R.string.week_to_display, formatDate(stats.startDate), formatDate(stats.endDate))
        }
    }

    override fun chooseStat(stat: Stat) {
        TODO("Not yet implemented")
    }
}