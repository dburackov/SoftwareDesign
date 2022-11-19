package com.dmburackov.timer

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.content.getSystemService
import java.util.*

class TimerService : Service() {
    private val timer = Timer()
    private lateinit var notificationManager : NotificationManager

    override fun onBind(p0: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        //val time = intent.getDoubleExtra(TIMER_EXTRA, 0.0)
        //timer.scheduleAtFixedRate(TimeTask(time), 0, 1000)

        showNotification()

        return START_NOT_STICKY
    }

    private fun createNotificationChannel(){
        val channel = NotificationChannel(CHANNEL_ID, "Timer service channel", NotificationManager.IMPORTANCE_HIGH)
        notificationManager = application?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    private fun showNotification() {
        val notificationIntent = Intent(this, MainActivity::class.java)

        val pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0)

        val notification = Notification.Builder(this, CHANNEL_ID)
            .setContentText("ABOBA")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentIntent(pendingIntent)
            .build()

        startForeground(NOTIFICATION_ID, notification)
    }

    private fun displayNotification(contentForHeader: String, contentForFooter:String){
        val notificationId = 13
        val notification = Notification.Builder(application, CHANNEL_ID)
            .setContentTitle(contentForHeader)
            .setContentText(contentForFooter)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setAutoCancel(true)
            .setPriority(Notification.PRIORITY_HIGH)
            .setOnlyAlertOnce(true)
            .setOngoing(true)
            .build()

        notificationManager?.notify(notificationId, notification)
    }

    fun start() {
        val time = Intent().getDoubleExtra(TIMER_EXTRA, 0.0)
        timer.scheduleAtFixedRate(TimeTask(time), 0, 1000)
        //timer.scheduleAtFixedRate(TimeTask(time), 0, 1000)
    }

    fun pause() {
        timer.cancel()
    }

    override fun onDestroy() {
        timer.cancel()
        notificationManager.cancelAll()
        Log.d("AAA", "service was destroy")
        super.onDestroy()
    }

    private inner class TimeTask(private var time : Double) : TimerTask() {
        override fun run() {
            val intent = Intent(TIMER_UPDATED)
            time++
            intent.putExtra(TIMER_EXTRA, time)
            displayNotification("$time","Repeats: /")
            sendBroadcast(intent)
        }

    }

    companion object {
        const val TIMER_UPDATED = "timerUpdated"
        const val TIMER_EXTRA = "timerExtra"
        const val CHANNEL_ID = "timerChannel"
        const val NOTIFICATION_ID = 13
    }
}