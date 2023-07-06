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
import com.soul.mvvmbase.ui.activity.MainActivity
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

const val USE_DEVICE_LOCATION ="USE_DEVICE_LOCATION"
const val CUSTOM_LOCATION ="CUSTOM_LOCATION"
class SettingsFragment : PreferenceFragmentCompat() ,Preference.OnPreferenceChangeListener{
    private val TAG = javaClass.simpleName
    private val  currentWeatherViewModel: CurrentWeatherViewModel by viewModel()
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preference, rootKey)
        Log.d("TAG", "bindUI: ${currentWeatherViewModel}")
        val USE_DEVICE_LOCATION: Preference? = findPreference(USE_DEVICE_LOCATION)
        val CUSTOM_LOCATION: Preference? = findPreference(CUSTOM_LOCATION)
        CUSTOM_LOCATION?.onPreferenceChangeListener = this
        USE_DEVICE_LOCATION?.onPreferenceChangeListener = this
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        Log.d("2222", "onActivityCreated: ")
        (activity as? AppCompatActivity)?.supportActionBar?.title ="Settings"
        (activity as? AppCompatActivity)?.supportActionBar?.subtitle =null

    }

    override fun onPreferenceChange(preference: Preference, newValue: Any?): Boolean {
        if(preference.key.equals(USE_DEVICE_LOCATION) && newValue as Boolean){
            (activity as MainActivity).restartLocation()
        }else{
            currentWeatherViewModel.reInitData()
        }
        Log.d("TAG", "onPreferenceChange: ")
        return true;
    }
}