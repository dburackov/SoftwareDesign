package com.dmburackov.timer

import android.content.Context
import android.media.MediaPlayer
import kotlin.random.Random

class SoundManager(private val context : Context) {
    private var soundPlayer : MediaPlayer? = null

    init {
        soundPlayer = MediaPlayer.create(context, R.raw.ah)
    }

    fun endSound() {
        soundPlayer = when (Random.nextInt(0, 4)) {
            0 -> {
                MediaPlayer.create(context, R.raw.ah)
            }
            1 -> {
                MediaPlayer.create(context, R.raw.augh)
            }
            2 -> {
                MediaPlayer.create(context, R.raw.penetration_1)
            }
            else -> {
                MediaPlayer.create(context, R.raw.yeeees)
            }
        }
        soundPlayer?.start()
    }

    fun workSound() {
        soundPlayer = when (Random.nextInt(0, 6)) {
            0 -> {
                MediaPlayer.create(context, R.raw.areeeeeee)
            }
            1 -> {
                MediaPlayer.create(context, R.raw.dont_stop)
            }
            2 -> {
                MediaPlayer.create(context, R.raw.move_move_move)
            }
            3 -> {
                MediaPlayer.create(context, R.raw.yeaaaauuuuuuuuuuuuuuuuh)
            }
            4 -> {
                MediaPlayer.create(context, R.raw.ladies_first)
            }
            else -> {
                MediaPlayer.create(context, R.raw.atteeen_tion)
            }
        }
        soundPlayer?.start()
    }

    fun restSound() {
        soundPlayer = when (Random.nextInt(0, 2)) {
            0 -> {
                MediaPlayer.create(context, R.raw.cumming)
            }
            else -> {
                MediaPlayer.create(context, R.raw.alright_man)
            }
        }
        soundPlayer?.start()
    }

    fun finishSound() {
        soundPlayer = MediaPlayer.create(context, R.raw.yeaaaauuuuuuuuuuuuuuuuh)
        soundPlayer?.start()
    }

}