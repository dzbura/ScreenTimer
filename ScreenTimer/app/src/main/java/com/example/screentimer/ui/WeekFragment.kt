package com.example.screentimer.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.screentimer.UsageStatsService
import com.example.screentimer.data.DayStat
import com.example.screentimer.data.WeekStat
import com.example.screentimer.databinding.FragmentWeekBinding
import com.example.screentimer.ui.adapters.DayStatAdapter
import com.example.screentimer.ui.adapters.DayStatItemClickListener
import java.time.LocalDate

class WeekFragment : Fragment(), DayStatItemClickListener {

    companion object {
        val TAG = "DayStat"
    }

    lateinit var mAdapter: DayStatAdapter
    private val sharedViewModel : MainViewModel by activityViewModels()
    private var binding : FragmentWeekBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val fragmentBinding = FragmentWeekBinding.inflate(inflater,container,false)
        mAdapter = DayStatAdapter(this)
        var stats: WeekStat = WeekStat(LocalDate.now(), LocalDate.now().minusDays(7), arrayListOf<DayStat>())
        if (UsageStatsService.checkUsageStatsPermission(requireContext())) {
            stats = UsageStatsService.getWeeksStats(context, 1)
        }
        binding = fragmentBinding
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
}