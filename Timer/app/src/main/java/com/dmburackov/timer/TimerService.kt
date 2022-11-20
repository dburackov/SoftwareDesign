package com.dmburackov.timer

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.CountDownTimer
import android.os.IBinder
import java.util.*

class TimerService : Service() {
    companion object {
        const val TIMER_UPDATED = "timerUpdated"
        const val WORKOUT = "workout"
        const val CURRENT_TIME = "currentTime"
        const val CURRENT_STAGE = "currentStage"
        const val CONTROL_ACTION = "controlAction"
        const val TIMER_START = 0
        const val TIMER_STOP = 1
        const val PREV_STAGE = 2
        const val NEXT_STAGE = 3
        const val CHANNEL_ID = "timerChannel"
        const val NOTIFICATION_ID = 13
    }

    private lateinit var notificationManager : NotificationManager
    private lateinit var workout : IntArray
    private var currentTime : Int = 0
    private var currentStage : Int = 0
    private var timer : CountDownTimer? = null

    override fun onBind(p0: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        workout = intent.getIntArrayExtra(WORKOUT)!!
        currentTime = workout.first()

        //showNotification("init timer value")

        registerReceiver(controlAction, IntentFilter(CONTROL_ACTION))
        return START_NOT_STICKY
    }

    fun startTimer() {
        //val time = Intent().getDoubleExtra(TIME, 0.0)
        //timer.scheduleAtFixedRate(TimeTask(time), 0, 1000)
        timer?.cancel()
        timer = object : CountDownTimer(currentTime.toLong() * 1000, 1) {
            override fun onTick(remaining: Long) {
                if (remaining / 1000 < currentTime) {
                    currentTime--
                    val intent = Intent(TIMER_UPDATED)
                    intent.putExtra(CURRENT_TIME, (currentTime + 1)) //currentTime + 1
                    val stageName = Workout.getStageName(currentStage, workout.size)
                    intent.putExtra(CURRENT_STAGE, stageName)
                    showNotification((currentTime + 1).toString(), stageName) //currentTime + 1
                    sendBroadcast(intent)
                }
            }
            override fun onFinish() {
                currentStage++
                if (currentStage < workout.size) {
                    currentTime = workout[currentStage]
                    startTimer()
                }
            }
        }.start()

    }

    fun stopTimer() {
        timer?.cancel()
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
                    showNotification("prev stage", "adsf")
                }
                NEXT_STAGE -> { //next stage
                    showNotification("next stage", "adsf")
                }
                else -> {} //default value
            }
        }

    }

    private fun createNotificationChannel(){
        val channel = NotificationChannel(CHANNEL_ID, "Timer service channel", NotificationManager.IMPORTANCE_HIGH)
        notificationManager = application?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    private fun showNotification(time : String, stage : String) {
        val notificationIntent = Intent(this, MainActivity::class.java)

        val pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0)

        val notification = Notification.Builder(this, CHANNEL_ID)
            .setContentTitle(time)
            .setContentText(stage)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentIntent(pendingIntent)
            .build()

        startForeground(NOTIFICATION_ID, notification)
    }

//    private inner class TimeTask(private var time : Double) : TimerTask() {
//        override fun run() {
//            val intent = Intent(TIMER_UPDATED)
//            time++
//            intent.putExtra(TIME, time)
//            displayNotification("$time","Repeats: /")
//            sendBroadcast(intent)
//        }
//
//    }
}