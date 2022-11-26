package com.dmburackov.timer

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.PreferenceManager
import java.util.*

class PrefManager(private val context : Context) {
    private val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

    fun updateLang(lang : String) {
        val locale = Locale(lang)
        Locale.setDefault(locale)
        val resources = context.resources
        val configuration = resources.configuration
        configuration.locale = locale
        resources.updateConfiguration(configuration, resources.displayMetrics)
    }

    fun getLang() : String {
        return sharedPreferences.getString("lang", "en").toString()
    }

    fun updateFont(size : Float) {
        val resources = context.resources
        val configuration = resources.configuration
        configuration.fontScale = size
        resources.updateConfiguration(configuration, resources.displayMetrics)
    }

    fun getFont(): Float {
        return sharedPreferences.getInt("font", 20) / 20f
    }

    fun updateTheme(theme : String) {
        //context.setTheme(R.style.Theme_Timer)
        if (theme == "light") {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        }
    }

    fun getTheme() : String {
        return sharedPreferences.getString("theme", "light").toString()
    }
}