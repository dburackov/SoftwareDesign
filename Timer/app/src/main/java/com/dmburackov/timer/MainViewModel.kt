package com.dmburackov.timer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.MutableLiveData

open class MainViewModel : ViewModel() {
    var workoutEdit = MutableLiveData<Int>()
    var workoutGo = MutableLiveData<Int>()

    lateinit var db : Database

    init {
        //temp.value = 0
    }
}