package com.example.screentimer.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
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
        if (sharedViewModel.stGoal.value != null) {
            fragmentBinding.seekBar.setProgress(sharedViewModel.stGoal.value!!/15)
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
        sharedViewModel._stGoal.value = binding!!.seekBar.progress*15
        //sharedViewModel._stGoal.value = binding!!.setGoalInput.text.toString().toIntOrNull()
        findNavController().navigate(R.id.action_setGoalFragment_to_todayFragment)
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