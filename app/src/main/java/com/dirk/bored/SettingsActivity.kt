package com.dirk.bored

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.*


class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)
        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.settings, SettingsFragment())
                .commit()
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    class SettingsFragment : PreferenceFragmentCompat(), SharedPreferences.OnSharedPreferenceChangeListener {
        override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
            preferenceManager.sharedPreferences.registerOnSharedPreferenceChangeListener(this)
            return super.onCreateView(inflater, container, savedInstanceState)
        }

        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey)
            initPrefSummaries()
        }

        override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) {
            Log.d("SettingsActivity", "setting '$key' changed")
            when(key) {
                "pref_participants" -> updatePrefParticipants()
                "pref_price" -> updatePrefPrice()
                "pref_accessibility" -> updatePrefAccessibility()
            }
        }

        private fun updatePrefParticipants() {
            val pref = findPreference<EditTextPreference>("pref_participants")
            pref?.summary = pref?.text
        }

        private fun updatePrefPrice() {
            val pref = findPreference<SeekBarPreference>("pref_price")
            val value = pref?.value
            if (value != null) {
                var summary = "error"
                summary = when {
                    value == 100 -> {
                        getString(R.string.price_summary_highest)
                    }
                    value > 66 -> {
                        getString(R.string.price_summary_high)
                    }
                    value > 33 -> {
                        getString(R.string.price_summary_middle)
                    }
                    value > 0 -> {
                        getString(R.string.price_summary_low)
                    }
                    else -> {
                        getString(R.string.price_summary_lowest)
                    }
                }
                pref?.summary = summary
            }
        }

        // Update summary of slider according to current value
        private fun updatePrefAccessibility() {
            val pref = findPreference<SeekBarPreference>("pref_accessibility")
            val value = pref?.value
            if (value != null) {
                var summary = "error"
                summary = when {
                    value == 100 -> {
                        getString(R.string.accessibility_summary_highest)
                    }
                    value > 66 -> {
                        getString(R.string.accessibility_summary_high)
                    }
                    value > 33 -> {
                        getString(R.string.accessibility_summary_middle)
                    }
                    value > 0 -> {
                        getString(R.string.accessibility_summary_low)
                    }
                    else -> {
                        getString(R.string.accessibility_summary_lowest)
                    }
                }
                pref?.summary = summary
            }
        }

        private fun initPrefSummaries() {
            updatePrefParticipants()
            updatePrefPrice()
            updatePrefAccessibility()
        }
    }
}