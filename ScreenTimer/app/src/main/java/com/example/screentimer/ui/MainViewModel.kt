package com.example.screentimer.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainViewModel : ViewModel() {

    var _stToday = MutableLiveData<Int>()
    val stToday : LiveData<Int> = _stToday

    var _weekOffset = MutableLiveData<Long>(1L)
    var weekOffset : LiveData<Long> = _weekOffset

    var _displayedWeekString = MutableLiveData<String>()
    var displayedWeekString : LiveData<String> = _displayedWeekString

    var _noData = MutableLiveData<String>()
    var noData : LiveData<String> = _noData

}