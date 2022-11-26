
package com.dmburackov.timer

import android.content.SharedPreferences
import android.os.Bundle
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import android.app.AlertDialog
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES
import androidx.fragment.app.activityViewModels

class PreferenceFragment : PreferenceFragmentCompat(), SharedPreferences.OnSharedPreferenceChangeListener {
    private val viewModel : MainViewModel by activityViewModels()

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)

        (activity as MainActivity).supportActionBar?.title = this.getString(R.string.settings)

        PreferenceManager.getDefaultSharedPreferences(requireContext())
            .registerOnSharedPreferenceChangeListener(this)

        val deleteButton: Preference? = findPreference("delete")

        deleteButton?.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            val dialogBuilder = AlertDialog.Builder(context)
            dialogBuilder.setMessage("Are you sure you want to delete all workouts?")
                .setCancelable(false)
                .setPositiveButton("delete") { _, _ ->
                    viewModel.db.clear()
                }
                .setNegativeButton("cancel") { dialog, _ ->
                    dialog.cancel()
                }
            val alert = dialogBuilder.create()
            alert.setTitle("Delete all workouts?")
            alert.show()
            true
        }

    }

    override fun onSharedPreferenceChanged(prefs: SharedPreferences?, key: String?) {
        when (key) {
            "theme" -> {
                activity?.recreate()
            }
            "lang" -> {
                activity?.recreate()
            }
            "font" -> {
                activity?.recreate()
            }
        }
    }

}