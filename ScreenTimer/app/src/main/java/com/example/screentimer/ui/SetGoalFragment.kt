package com.example.screentimer.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.screentimer.R
import com.example.screentimer.databinding.FragmentSetGoalBinding


class SetGoalFragment : Fragment() {

    private val sharedViewModel : MainViewModel by activityViewModels()
    private var binding : FragmentSetGoalBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val fragmentBinding = FragmentSetGoalBinding.inflate(inflater,container,false)
        val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE)
        val dailyGoal = sharedPref?.getInt(getString(R.string.preference_daily_goal), 0)
        if (dailyGoal != null) {
            fragmentBinding.seekBar.setProgress(dailyGoal/15)
        } else {
            fragmentBinding.seekBar.setProgress(0)
        }
        fragmentBinding.seekBar.setOnSeekBarChangeListener( object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {
                fragmentBinding.seekbarValue.text = formatTime(fragmentBinding.seekBar.progress*15)
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
        fragmentBinding.seekBar.setMax(32)
        binding = fragmentBinding
        return fragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.apply{
            lifecycleOwner = viewLifecycleOwner
            viewModel = sharedViewModel
            mainFragment = this@SetGoalFragment
        }
    }

    fun saveGoal() {
        val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE) ?: return
        with (sharedPref.edit()) {
            putInt(getString(R.string.preference_daily_goal), binding!!.seekBar.progress*15)
            apply()
        }
        Toast.makeText(context, "Your new daily goal was saved", Toast.LENGTH_SHORT).show()
    }

    fun formatTime(minutes : Int) : String {
        var formattedTime = ""
        if (minutes < 60) {
            formattedTime = getString(R.string.minutes_time_format, minutes)
        } else {
            formattedTime = if (minutes.rem(60) > 0)  getString(R.string.hours_time_format, minutes / 60) + " " + getString(R.string.minutes_time_format, minutes.rem(60)) else getString(R.string.hours_time_format, minutes / 60)
        }
        return formattedTime
    }

}