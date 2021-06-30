package com.example.screentimer.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainViewModel : ViewModel() {

    var _stToday = MutableLiveData<Int>()
    val stToday : LiveData<Int> = _stToday

    var _stGoal = MutableLiveData<Int>()
    var stGoal : LiveData<Int> = _stGoal

    var _weekOffset = MutableLiveData<Long>(1L)
    var weekOffset : LiveData<Long> = _weekOffset

}