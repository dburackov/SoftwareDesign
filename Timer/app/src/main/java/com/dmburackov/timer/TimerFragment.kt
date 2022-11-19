package com.dmburackov.timer

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.dmburackov.timer.databinding.FragmentTimerBinding
import kotlin.math.roundToInt

class TimerFragment : Fragment() {
    private lateinit var binding: FragmentTimerBinding
    private val viewModel : MainViewModel by activityViewModels()
    private lateinit var workout: Workout

    private var timerStarted = false
    private lateinit var serviceIntent: Intent
    private var time = 0.0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentTimerBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        workout = viewModel.db.getWorkoutById(viewModel.workoutGo.value!!.toInt())

        (activity as MainActivity).supportActionBar?.setBackgroundDrawable(ColorDrawable(Color.parseColor("#${workout.color}")))
        (activity as MainActivity).supportActionBar?.title = workout.title
        binding.mainLayout.background = ColorDrawable(Color.parseColor("#${workout.color}"))

        initButtons()

        serviceIntent = Intent(context, TimerService::class.java)
        //activity?.registerReceiver(updateTime, IntentFilter(TimerService.TIMER_UPDATED))
    }

    private fun initButtons() {
        binding.apply {
            startStopButton.setOnClickListener {
                if (timerStarted) {
                    stopTimer()
                } else {
                    startTimer()
                }
            }
            nextButton.setOnClickListener {

            }
            prevButton.setOnClickListener {

            }
        }
    }

    private fun startTimer() {
       // activity?.startService(serviceIntent)
        //serviceIntent.putExtra("a", 0)

        //activity?.startService(Intent(context, TimerService::class.java))

        //serviceIntent.putExtra(TimerService.TIMER_EXTRA, time)
        //activity?.startService(serviceIntent)
        timerStarted = true
        binding.startStopButton.setImageResource(R.drawable.ic_pause_70)
    }

    private fun stopTimer() {
        activity?.stopService(Intent(context, TimerService::class.java))
        //activity?.stopService(serviceIntent)
        timerStarted = false
        binding.startStopButton.setImageResource(R.drawable.ic_play_arrow_70)
    }

    private val updateTime : BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            time = intent.getDoubleExtra(TimerService.TIMER_EXTRA, 0.0)
            binding.timeText.text = time.roundToInt().toString()
        }
    }
}