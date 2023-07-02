package com.soul.mvvmbase.data.provider

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.preference.ListPreference
import androidx.preference.PreferenceManager
import androidx.preference.PreferenceScreen
import com.soul.mvvmbase.R

class LocationProviderImpl(context:Context) : LocationProvider {
    private val appContext =context.applicationContext
    private val namestringArray:Array<String> = appContext.resources.getStringArray(R.array.location_name)
    private val valuestringArray:Array<String> = appContext.resources.getStringArray(R.array.location_value)
    private val preference:SharedPreferences
        get() = PreferenceManager.getDefaultSharedPreferences(appContext)


    override fun getLocationName()= namestringArray.get(valuestringArray.indexOf(getLocationCode()))
    override fun getLocationCode()= preference.getString("CUSTOM_LOCATION","101010100")!!

    override fun WhetherUseDeviecLocation() = preference.getBoolean("USE_DEVICE_LOCATION",true)

    override val currentLocationName: MutableLiveData<String>
        get()=_currentLocationName

    private val _currentLocationName = MutableLiveData<String>()
}