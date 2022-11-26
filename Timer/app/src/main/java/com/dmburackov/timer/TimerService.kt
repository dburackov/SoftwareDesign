package com.dmburackov.timer

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.res.Resources
import android.os.CountDownTimer
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import java.util.*

class TimerService : Service() {
    companion object {
        const val TIMER_UPDATED = "timerUpdated"
        const val WORKOUT = "workout"
        const val CURRENT_TIME = "currentTime"
        const val CURRENT_STAGE = "currentStage"
        const val CONTROL_ACTION = "controlAction"
        const val WORKOUT_ID = "workoutId"
        const val TIMER_START = 0
        const val TIMER_STOP = 1
        const val PREV_STAGE = 2
        const val NEXT_STAGE = 3
        const val GET_WORKOUT = "getWorkout"
        const val RETURN_WORKOUT = "returnWorkout"
        const val TIMER_STATE = "timerState"
        const val CHANNEL_ID = "timerChannel"
        const val NOTIFICATION_ID = 13
    }

    private lateinit var notificationManager : NotificationManager
    private lateinit var workout : IntArray
    private lateinit var soundManager: SoundManager
    private var workoutId : Int = 0
    private var currentTime : Int = 0
    private var currentStage : Int = 0
    private var timerStarted : Boolean = false
    private var timer : CountDownTimer? = null

    override fun onBind(p0: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        initSound()
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        workoutId = intent.getIntExtra(WORKOUT_ID, -1)
        workout = intent.getIntArrayExtra(WORKOUT)!!
        currentTime = workout.first()

        registerReceiver(controlAction, IntentFilter(CONTROL_ACTION))
        return START_NOT_STICKY
    }

    private fun startTimer() {
        timer?.cancel()
        timerStarted = true
        timer = object : CountDownTimer(currentTime.toLong() * 1000, 1) {
            override fun onTick(remaining: Long) {
                if (remaining / 1000 < currentTime) {
                    currentTime--
                    val intent = Intent(TIMER_UPDATED)
                    intent.putExtra(CURRENT_TIME, (currentTime + 1)) //currentTime + 1
                    intent.putExtra(CURRENT_STAGE, currentStage)
                    val stageName = Workout.getStageName(currentStage, workout.size)
                    showNotification((currentTime + 1).toString(), stageName) //currentTime + 1
                    sendBroadcast(intent)
                    if (currentTime in 0..2) {
                        soundManager.endSound()
                    }
                }
            }
            override fun onFinish() {
                currentStage++
                if (currentStage < workout.size) {
                    currentTime = workout[currentStage]
                    if (currentStage % 2 == 1) {
                        soundManager.workSound()
                    } else {
                        soundManager.restSound()
                    }
                    startTimer()
                } else {
                    finish()
                }
            }
        }.start()
    }

    private fun finish() {
        soundManager.finishSound()
        val intent = Intent(TIMER_UPDATED)
        intent.putExtra(CURRENT_TIME, -1) //finish
        stopTimer()
        sendBroadcast(intent)
        showNotification("Finish!", "Finish!")
        Handler(Looper.getMainLooper()).postDelayed({
            this.stopSelf()
        }, 3000)
    }

    private fun stopTimer() {
        timerStarted = false
        timer?.cancel()

        val stageName = Workout.getStageName(currentStage, workout.size)
        showNotification((currentTime + 1).toString(), stageName)
    }

    private fun prevStage() {
        if (currentStage > 0) {
            if (timerStarted) {
                timer?.cancel()
            }
            currentStage--
            currentTime = workout[currentStage]
            if (timerStarted) {
                startTimer()
            }
            val intent = Intent(TIMER_UPDATED)
            intent.putExtra(CURRENT_TIME, currentTime) //currentTime + 1
            intent.putExtra(CURRENT_STAGE, currentStage)
            val stageName = Workout.getStageName(currentStage, workout.size)
            showNotification(currentTime.toString(), stageName) //currentTime + 1
            sendBroadcast(intent)
        }
    }

    private fun nextStage() {
        if (currentStage + 1 < workout.size) {
            if (timerStarted) {
                timer?.cancel()
            }
            currentStage++
            currentTime = workout[currentStage]
            if (timerStarted) {
                startTimer()
            }
            val intent = Intent(TIMER_UPDATED)
            intent.putExtra(CURRENT_TIME, currentTime) //currentTime + 1
            intent.putExtra(CURRENT_STAGE, currentStage)
            val stageName = Workout.getStageName(currentStage, workout.size)
            showNotification(currentTime.toString(), stageName) //currentTime + 1
            sendBroadcast(intent)
        } else {
            finish()
        }
    }

    override fun onDestroy() {
        timer?.cancel()
        notificationManager.cancelAll()
        super.onDestroy()
    }

    private val controlAction : BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when (intent.getIntExtra(CONTROL_ACTION, -1)) {
                TIMER_START -> { //start timer
                    startTimer()
                }
                TIMER_STOP -> { //stop timer
                    stopTimer()
                }
                PREV_STAGE -> { //prev stage
                    prevStage()
                }
                NEXT_STAGE -> { //next stage
                    nextStage()
                }
                else -> {} //default value
            }
        }
    }

    private fun initSound() {
        soundManager = SoundManager(this)
    }

    private fun createNotificationChannel(){
        val channel = NotificationChannel(CHANNEL_ID, "Timer service channel", NotificationManager.IMPORTANCE_HIGH)
        notificationManager = application?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    private fun showNotification(time : String, stage : String) {
        val notificationIntent = Intent(this, MainActivity::class.java)
        notificationIntent.putExtra(WORKOUT_ID, workoutId)
        notificationIntent.putExtra(CURRENT_STAGE, currentStage)
        notificationIntent.putExtra(CURRENT_TIME, currentTime)
        notificationIntent.putExtra(TIMER_STATE, timerStarted)
        val pendingIntent = PendingIntent.getActivity(baseContext, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        val notification = Notification.Builder(this, CHANNEL_ID)
            .setContentTitle(time)
            .setContentText(stage)
            .setSmallIcon(R.drawable.ic_access_alarm_24)
            .setContentIntent(pendingIntent)
            .build()

        startForeground(NOTIFICATION_ID, notification)
    }
}