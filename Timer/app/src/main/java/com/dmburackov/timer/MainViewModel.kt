package com.dmburackov.timer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.MutableLiveData

open class MainViewModel : ViewModel() {
    var workoutEdit : Int = 0
    var workoutRun : Int? = null

    var workoutId = 0
    var currentTime = 0
    var currentStage = 0
    var timerStarted = true

    lateinit var db : Database
}