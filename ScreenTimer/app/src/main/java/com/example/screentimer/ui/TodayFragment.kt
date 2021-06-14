package com.example.screentimer.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.screentimer.R
import com.example.screentimer.databinding.FragmentTodayBinding

class TodayFragment : Fragment() {

    private val sharedViewModel : MainViewModel by activityViewModels()

    private var binding : FragmentTodayBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val fragmentBinding = FragmentTodayBinding.inflate(inflater,container, false)
        binding = fragmentBinding
        fragmentBinding.textView.text = sharedViewModel.stGoal.value.toString()
        fragmentBinding.gauge1.endValue = if (sharedViewModel.stGoal.value != null) sharedViewModel.stGoal.value!! else 100
        return fragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = sharedViewModel
            mainFragment = this@TodayFragment
        }
        Log.d("sfds", sharedViewModel._stGoal.value.toString())
    }

    fun setGoal() {
        findNavController().navigate(R.id.action_todayFragment_to_setGoalFragment)
    }
}