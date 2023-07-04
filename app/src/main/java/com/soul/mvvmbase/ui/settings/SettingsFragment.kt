package com.soul.mvvmbase.ui.settings

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.soul.mvvmbase.R
import com.soul.mvvmbase.data.viewmodel.CurrentWeatherViewModel
import com.soul.mvvmbase.data.viewmodel.CurrentWeatherViewModelFactory
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel


class SettingsFragment : PreferenceFragmentCompat() {
    private val  currentWeatherViewModel: CurrentWeatherViewModel by viewModel()
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preference, rootKey)
        Log.d("TAG", "bindUI: ${currentWeatherViewModel}")
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        Log.d("2222", "onActivityCreated: ")
        (activity as? AppCompatActivity)?.supportActionBar?.title ="Settings"
        (activity as? AppCompatActivity)?.supportActionBar?.subtitle =null

    }



}