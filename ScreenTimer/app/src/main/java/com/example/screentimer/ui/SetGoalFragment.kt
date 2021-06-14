package com.example.screentimer.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
        binding = fragmentBinding
        // Inflate the layout for this fragment

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
        sharedViewModel._stGoal.value = binding!!.setGoalInput.text.toString().toIntOrNull()
        findNavController().navigate(R.id.action_setGoalFragment_to_todayFragment)
    }

}