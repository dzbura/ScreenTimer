package com.example.screentimer.ui

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.screentimer.R
import com.example.screentimer.UsageStatsService
import com.example.screentimer.data.DayStat
import com.example.screentimer.data.WeekStat
import com.example.screentimer.databinding.FragmentMonthBinding
import com.example.screentimer.formatDate
import com.example.screentimer.ui.adapters.WeekStatItemClickListener
import com.jjoe64.graphview.DefaultLabelFormatter
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries
import java.time.LocalDate

class MonthFragment : Fragment(), WeekStatItemClickListener {

    companion object {
        val TAG = "WeekStat"
    }

    private val sharedViewModel : MainViewModel by activityViewModels()
    private var binding : FragmentMonthBinding? = null
    lateinit var series1: LineGraphSeries<DataPoint>

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val fragmentBinding = FragmentMonthBinding.inflate(inflater,container,false)
        binding = fragmentBinding
        series1 = LineGraphSeries<DataPoint>()
        var stats1 : WeekStat = WeekStat(LocalDate.MAX, LocalDate.MAX, arrayListOf<DayStat>(),"", 0L)
        if (UsageStatsService.checkUsageStatsPermission(requireContext())) {
            stats1 = UsageStatsService.getWeeksStats(context, sharedViewModel._weekOffset.value!!)
            for (i in 0..6) {
                series1.appendData((DataPoint(i.toDouble(), (stats1.stats[i].toalTime/60000L).toDouble())), true, 20)
            }
            binding!!.graph.gridLabelRenderer.numHorizontalLabels = 7
            binding!!.graph.viewport.isXAxisBoundsManual = true
            binding!!.graph.viewport.setMaxX(6.0)
            binding!!.graph.viewport.setMinX(0.0)
            binding!!.graph.viewport.setMaxY(1440.0)
            binding!!.graph.gridLabelRenderer.labelFormatter = WeekLabelFormatter(requireContext())
            binding!!.graph.addSeries(series1)
        }

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

    fun navigateWeeks(offset : Long) {
        if (offset >= 0L || sharedViewModel._weekOffset.value!! >= 0L) {
            var stats: WeekStat = WeekStat(LocalDate.now(), LocalDate.now().minusDays(7), arrayListOf<DayStat>(), "", 0L)
            if (offset == 0L) {
                sharedViewModel._weekOffset.value = 1L
            } else {
                sharedViewModel._weekOffset.value = sharedViewModel._weekOffset.value?.plus(offset)
            }
            if (UsageStatsService.checkUsageStatsPermission(requireContext())) {
                stats = UsageStatsService.getWeeksStats(context, sharedViewModel._weekOffset.value!!)
                if (stats.stats.isEmpty()) {
                    sharedViewModel._noData.value = getString(R.string.no_data_for_week)
                    binding!!.graph.visibility = INVISIBLE
                } else if (stats.stats.size < 6) {
                    sharedViewModel._noData.value = getString(R.string.not_enough_data_for_week)
                    binding!!.graph.visibility = INVISIBLE
                } else {
                    binding!!.graph.visibility = VISIBLE
                    sharedViewModel._displayedWeekString.value = getString(R.string.week_to_display, formatDate(stats.startDate), formatDate(stats.endDate))
                    series1 = LineGraphSeries<DataPoint>()
                    for (i in 0..6) {
                        series1.appendData((DataPoint(i.toDouble(), (stats.stats[i].toalTime/60000L).toDouble())), true, 20)
                    }
                    binding!!.graph.removeAllSeries()
                    binding!!.graph.addSeries(series1)
                    binding!!.graph.refreshDrawableState()
                    sharedViewModel._displayedAverageString.value = stats.dailyMeanString
                    sharedViewModel._noData.value = ""
                }
            } else {
                startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS))
            }
        }
    }
}

class WeekLabelFormatter(context: Context) : DefaultLabelFormatter() {
    var weekDayList: Array<String> = context.resources.getStringArray(R.array.weekday_labels_en) as Array<String>
    val weekDayIndex = LocalDate.now().dayOfWeek.value - 1

    override fun formatLabel(value: Double, isValueX: Boolean): String {
        var label = ""
        if (isValueX) {
            label = weekDayList[(value+weekDayIndex).rem(7).toInt()]
        } else {
            label = (value/60).toString().substringBefore(".") + ":" + value.rem(60).toInt().toString()
        }
        return label
    }
}