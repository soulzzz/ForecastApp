package com.soul.mvvmbase.data.provider


import androidx.lifecycle.MutableLiveData
import com.soul.mvvmbase.data.bean.WeatherLocation

interface LocationProvider {
    suspend fun hasLocationChanged(weatherLocation :WeatherLocation):Boolean
    suspend fun getPreferredLocationString(): String
    fun getSelectedLocationName():String
    fun getSelectedLocationCode():String

    fun isUsingDeviceLocation():Boolean
}