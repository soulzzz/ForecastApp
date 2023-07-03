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

class LocationProviderImpl(context:Context) : LocationProvider {
    private val appContext =context.applicationContext
    private val namestringArray:Array<String> = appContext.resources.getStringArray(R.array.location_name)
    private val valuestringArray:Array<String> = appContext.resources.getStringArray(R.array.location_value)
    private val preference:SharedPreferences
        get() = PreferenceManager.getDefaultSharedPreferences(appContext)


    override fun getSelectedLocationName()= namestringArray.get(valuestringArray.indexOf(getSelectedLocationCode()))
    override fun getSelectedLocationCode()= preference.getString("CUSTOM_LOCATION","101010100")!!

    override fun WhetherUseDeviecLocation() = preference.getBoolean("USE_DEVICE_LOCATION",true)
    override fun getAutoLocationName(): String {
        return preference.getString("AUTO_LOCATION_NAME","北京")!!
    }

    override fun getAutoLocationCode(): String {
        return preference.getString("AUTO_LOCATION_CODE","116.41,39.92")!!
    }

    override fun setAutoLocationName(name: String) {
        preference.edit {
            putString("AUTO_LOCATION_NAME",name)
        }
    }

    override fun setAutoLocationCode(code: String) {
        preference.edit {
            putString("AUTO_LOCATION_CODE",code)
        }
    }

    override val currentLocationName: MutableLiveData<String>
        get()=_currentLocationName

    private val _currentLocationName = MutableLiveData<String>()
}