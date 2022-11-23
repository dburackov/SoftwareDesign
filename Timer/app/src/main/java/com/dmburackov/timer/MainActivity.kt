package com.dmburackov.timer

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupActionBarWithNavController

class MainActivity : AppCompatActivity() {
    private val viewModel : MainViewModel by viewModels()
    lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewModel.db = Database(this)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragment_container) as NavHostFragment
        navController = navHostFragment.navController

        setupActionBarWithNavController(navController)

        val currentWorkout = intent.getIntExtra(TimerService.WORKOUT_ID, -1)
        if (currentWorkout >= 0) {
            viewModel.workoutId = currentWorkout
            viewModel.currentTime = intent.getIntExtra(TimerService.CURRENT_TIME, 0)
            viewModel.currentStage = intent.getIntExtra(TimerService.CURRENT_STAGE, 0)
            viewModel.timerStarted = intent.getBooleanExtra(TimerService.TIMER_STATE, true)
            navController.navigate(R.id.action_mainFragment_to_timerFragment)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

}