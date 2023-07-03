package com.soul.mvvmbase.data.provider

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.preference.ListPreference
import androidx.preference.PreferenceManager
import androidx.preference.PreferenceScreen
import com.soul.mvvmbase.R

const val USE_DEVICE_LOCATION = "USE_DEVICE_LOCATION"
const val CUSTOM_LOCATION = "CUSTOM_LOCATION"
class LocationProviderImpl(context:Context,

) : LocationProvider {
    override val use_device_location: MutableLiveData<Boolean>
        get() =_use_device_location
    private val appContext =context.applicationContext
    private val namestringArray:Array<String> = appContext.resources.getStringArray(R.array.location_name)
    private val valuestringArray:Array<String> = appContext.resources.getStringArray(R.array.location_value)
    private val preference:SharedPreferences
        get() = PreferenceManager.getDefaultSharedPreferences(appContext)


    override fun getSelectedLocationName()= namestringArray.get(valuestringArray.indexOf(getSelectedLocationCode()))
    override fun getSelectedLocationCode()= preference.getString(CUSTOM_LOCATION,"101010100")!!



    private val _use_device_location = MutableLiveData<Boolean>()
}