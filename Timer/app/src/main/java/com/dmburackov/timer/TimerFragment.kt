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
        binding.timeText.text = workout.warmup.toString()
        binding.stageText.text = "Warm up"

        initButtons()

        startService()

        activity?.registerReceiver(updateTime, IntentFilter(TimerService.TIMER_UPDATED))
    }

    private fun startService() {
        serviceIntent = Intent(context, TimerService::class.java)
        serviceIntent.putExtra(TimerService.WORKOUT, workout.getValues())
        activity?.startService(serviceIntent)
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
                nextStage()
            }
            prevButton.setOnClickListener {
                prevStage()
            }
        }
    }

    private fun startTimer() {
        val intent = Intent(TimerService.CONTROL_ACTION)
        intent.putExtra(TimerService.CONTROL_ACTION, TimerService.TIMER_START)
        activity?.sendBroadcast(intent)

        timerStarted = true
        binding.startStopButton.setImageResource(R.drawable.ic_pause_70)
    }

    private fun stopTimer() {
        val intent = Intent(TimerService.CONTROL_ACTION)
        intent.putExtra(TimerService.CONTROL_ACTION, TimerService.TIMER_STOP)
        activity?.sendBroadcast(intent)

        timerStarted = false
        binding.startStopButton.setImageResource(R.drawable.ic_play_arrow_70)
    }

    private fun prevStage() {
        //check
        val intent = Intent(TimerService.CONTROL_ACTION)
        intent.putExtra(TimerService.CONTROL_ACTION, TimerService.PREV_STAGE)
        activity?.sendBroadcast(intent)
        //recycler view something do
    }

    private fun nextStage() {
        //check
        val intent = Intent(TimerService.CONTROL_ACTION)
        intent.putExtra(TimerService.CONTROL_ACTION, TimerService.NEXT_STAGE)
        activity?.sendBroadcast(intent)
        //recycler view ....
    }

    private val updateTime : BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val time = intent.getIntExtra(TimerService.CURRENT_TIME, 0)
            val stage = intent.getStringExtra(TimerService.CURRENT_STAGE)
            binding.timeText.text = time.toString()
            binding.stageText.text = stage
        }
    }
}