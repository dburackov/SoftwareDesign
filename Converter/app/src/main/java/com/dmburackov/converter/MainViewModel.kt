package com.dmburackov.converter

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

open class MainViewModel : ViewModel() {
    val newKey: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    val fromText: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    val toText: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    var currentCategory : Int = 0

    var currentFromUnit : Int = 0

    var currentToUnit : Int = 0

    val showSwapButton : MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>(true)
    }
}