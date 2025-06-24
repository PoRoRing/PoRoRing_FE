package com.example.pororing

import android.os.Bundle
import androidx.preference.EditTextPreference
import androidx.preference.ListPreference
import androidx.preference.PreferenceFragmentCompat

class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)

        val bgColorPreference = findPreference<ListPreference>("color")
        val namePreference = findPreference<EditTextPreference>("name")
        val fontSizePreference = findPreference<ListPreference>("font_size")
        val fontStylePreference = findPreference<ListPreference>("font_style")

        namePreference?.summaryProvider = EditTextPreference.SimpleSummaryProvider.getInstance()
        bgColorPreference?.summaryProvider = ListPreference.SimpleSummaryProvider.getInstance()
        fontSizePreference?.summaryProvider = ListPreference.SimpleSummaryProvider.getInstance()
        fontStylePreference?.summaryProvider = ListPreference.SimpleSummaryProvider.getInstance()
    }
}

