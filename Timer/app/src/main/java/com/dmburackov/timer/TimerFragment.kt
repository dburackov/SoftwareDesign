package com.dmburackov.timer

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build.VERSION_CODES.P
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.*
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.core.view.MenuProvider
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.dmburackov.timer.databinding.FragmentTimerBinding

class TimerFragment : Fragment(), MenuProvider {
    private lateinit var binding: FragmentTimerBinding
    private val viewModel : MainViewModel by activityViewModels()
    private lateinit var workout: Workout
    private lateinit var adapter: StageAdapter

    private var timerStarted = false
    private lateinit var serviceIntent: Intent

    private var currentTime : Int = 0
    private var currentStage : Int = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentTimerBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.RESUMED)

        setupOnBackPressed()

        if (viewModel.workoutRun != null) {
            workout = viewModel.db.getWorkoutById(viewModel.workoutRun!!)
            startService()
        } else {
            serviceIntent = Intent(context, TimerService::class.java)
            workout = viewModel.db.getWorkoutById(viewModel.workoutId)
            currentTime = viewModel.currentTime
            currentStage = viewModel.currentStage
            timerStarted = viewModel.timerStarted
        }

        (activity as MainActivity).supportActionBar?.setBackgroundDrawable(ColorDrawable(Color.parseColor("#${workout.color}")))
        (activity as MainActivity).supportActionBar?.title = workout.title
        binding.mainLayout.background = ColorDrawable(Color.parseColor("#${workout.color}"))
        binding.timeText.text = workout.warmup.toString()
        binding.stageText.text = "Warm up"

        binding.stageRecycler.layoutManager = LinearLayoutManager(context)
        adapter = StageAdapter(workout)
        binding.stageRecycler.adapter = adapter

        initButtons()

        if (viewModel.workoutRun == null) {
            if (timerStarted) {
                binding.startStopButton.setImageResource(R.drawable.ic_pause_70)
            } else {
                binding.startStopButton.setImageResource(R.drawable.ic_play_arrow_70)
            }

            binding.timeText.text = currentTime.toString()
            binding.stageText.text = Workout.getStageName(currentStage, workout.size())
            adapter.setCurrentStage(currentStage)
        }

        activity?.registerReceiver(updateTime, IntentFilter(TimerService.TIMER_UPDATED))


    }

    private fun startService() {
        serviceIntent = Intent(context, TimerService::class.java)
        serviceIntent.putExtra(TimerService.WORKOUT, workout.getValues())
        serviceIntent.putExtra(TimerService.WORKOUT_ID, workout.id)
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

    private fun setupOnBackPressed() {
        requireActivity().onBackPressedDispatcher.addCallback(this) {
            activity?.stopService(serviceIntent)
            findNavController().navigate(R.id.action_timerFragment_to_mainFragment)
            isEnabled = false
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
        val intent = Intent(TimerService.CONTROL_ACTION)
        intent.putExtra(TimerService.CONTROL_ACTION, TimerService.PREV_STAGE)
        activity?.sendBroadcast(intent)
        //recycler view something do
    }

    private fun nextStage() {
        val intent = Intent(TimerService.CONTROL_ACTION)
        intent.putExtra(TimerService.CONTROL_ACTION, TimerService.NEXT_STAGE)
        activity?.sendBroadcast(intent)
        //recycler view ....
    }

    private val updateTime : BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            currentTime = intent.getIntExtra(TimerService.CURRENT_TIME, 0)
            currentStage = intent.getIntExtra(TimerService.CURRENT_STAGE, 0)
            if (currentTime == -1) {
                binding.stageText.text = "Finish!"
                binding.timeText.text = ""
                binding.nextButton.isVisible = false
                binding.prevButton.isVisible = false
                binding.startStopButton.isVisible = false
                binding.stageRecycler.isVisible = false
            } else {
                binding.timeText.text = currentTime.toString()
                binding.stageText.text = Workout.getStageName(currentStage, workout.size())
                adapter.setCurrentStage(currentStage)
                binding.stageRecycler.layoutManager?.scrollToPosition(currentStage)
            }
        }
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.timer_fragment_menu, menu)
    }



    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        return when (menuItem.itemId) {
            R.id.cancel_item -> {
                activity?.stopService(serviceIntent)
                findNavController().navigate(R.id.action_timerFragment_to_mainFragment)
                true
            }
            android.R.id.home -> {
                activity?.stopService(serviceIntent)
                findNavController().navigate(R.id.action_timerFragment_to_mainFragment)
                true
            }
            else -> false
        }
    }
}