package com.soul.mvvmbase.data.provider

import android.content.Context
import android.content.SharedPreferences
import android.location.Location
import android.util.Log
import androidx.core.content.edit
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.preference.ListPreference
import androidx.preference.PreferenceManager
import androidx.preference.PreferenceScreen
import com.amap.api.location.AMapLocation
import com.amap.api.location.AMapLocationClient
import com.soul.mvvmbase.R
import com.soul.mvvmbase.data.bean.WeatherLocation
import kotlinx.coroutines.Deferred
import java.math.BigDecimal

const val USE_DEVICE_LOCATION = "USE_DEVICE_LOCATION"
const val CUSTOM_LOCATION = "CUSTOM_LOCATION"
class LocationProviderImpl(context:Context,
    private val aMapLocationClient: AMapLocationClient
) : LocationProvider {
    private val TAG = javaClass.simpleName
    private val appContext =context.applicationContext
    private val namestringArray:Array<String> = appContext.resources.getStringArray(R.array.location_name)
    private val valuestringArray:Array<String> = appContext.resources.getStringArray(R.array.location_value)
    private val preference:SharedPreferences
        get() = PreferenceManager.getDefaultSharedPreferences(appContext)

    override suspend fun hasLocationChanged(weatherLocation: WeatherLocation): Boolean {
        val deviceLocationChanged = try {
            hasDeviceLocationChanged(weatherLocation)
        } catch (e: java.lang.Exception) {
            false
        }

        return deviceLocationChanged || hasCustomLocationChanged(weatherLocation)
    }
    private suspend fun hasDeviceLocationChanged(lastWeatherLocation: WeatherLocation): Boolean {
        if (!isUsingDeviceLocation())
            return false

        val deviceLocation = getLastDeviceLocation()
            ?: return false

        // Comparing doubles cannot be done with "=="
        val comparisonThreshold = 0.03
        return Math.abs(deviceLocation.latitude.roundTo2DecimalPlaces() - lastWeatherLocation.latitude.toDouble()) > comparisonThreshold &&
                Math.abs(deviceLocation.longitude.roundTo2DecimalPlaces() - lastWeatherLocation.longitude.toDouble()) > comparisonThreshold
    }

    private fun hasCustomLocationChanged(lastWeatherLocation: WeatherLocation): Boolean {
        if (!isUsingDeviceLocation()) {
            val customLocationName = getSelectedLocationName()
            return ! (customLocationName.contains(lastWeatherLocation.city)  || lastWeatherLocation.city.contains(customLocationName) )
        }
        return false
    }
    override suspend fun getPreferredLocationString(): String {
        Log.d(TAG, "getPreferredLocationString: ")
        if (isUsingDeviceLocation()) {
            try {
                var deviceLocation = getLastDeviceLocation()
                if(deviceLocation.longitude.equals("null") || deviceLocation.city == null){
                    Log.d(TAG, "getSelectedLocationCode: ")
                    return getSelectedLocationCode()
                }
                Log.d(TAG, "getPreferredLocationString: ")
                return "${deviceLocation.longitude.roundTo2DecimalPlaces()},${deviceLocation.latitude.roundTo2DecimalPlaces()}"
            } catch (e: java.lang.Exception) {
                return getSelectedLocationCode()
            }
        }
        else{
            Log.d(TAG, "getPreferredLocationString: getSelectedLocationCode ${getSelectedLocationCode()}")
            return getSelectedLocationCode()
        }

    }

    private fun getLastDeviceLocation(): AMapLocation {
       return aMapLocationClient.lastKnownLocation
    }
    override fun getSelectedLocationName()= namestringArray.get(valuestringArray.indexOf(getSelectedLocationCode()))
    override fun getSelectedLocationCode()= preference.getString(CUSTOM_LOCATION,"101010100")!!

     override fun isUsingDeviceLocation(): Boolean {
        return preference.getBoolean(USE_DEVICE_LOCATION, true)
    }
    fun Double.roundTo2DecimalPlaces() =
        BigDecimal(this).setScale(2, BigDecimal.ROUND_HALF_UP).toDouble()

}