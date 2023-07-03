package com.soul.mvvmbase.data.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.soul.mvvmbase.data.bean.WeatherLocation
import com.soul.mvvmbase.data.network.repository.ForecastRepository
import com.soul.mvvmbase.data.provider.LocationProvider
import kotlinx.coroutines.*

class CurrentWeatherViewModel(
    private val forecastRepository: ForecastRepository,
    val locationProvider: LocationProvider
): ViewModel() {
    var weather =  GlobalScope.async(Dispatchers.IO,start = CoroutineStart.LAZY) {
        forecastRepository.getCurrentWeather(
            if(locationProvider.use_device_location.value == true){
                var tmp = forecastRepository.getWeatherLocation().value
                tmp?.longitude.toString()+","+tmp?.longitude.toString()
            }else{
                locationProvider.getSelectedLocationCode()
            }
        )
    }
    var weatherLocation = GlobalScope.async(Dispatchers.IO,start = CoroutineStart.LAZY) {
            forecastRepository.getWeatherLocation()
        }



    fun fetchNewWeatherWhenLocationChanged(){
        Log.d("TAG", "fetchNewWeatherWhenLocationChanged: ")
            weather =  GlobalScope.async(Dispatchers.IO,start = CoroutineStart.LAZY) {
                forecastRepository.getCurrentWeather(if(locationProvider.use_device_location.value == true){
                    var tmp = forecastRepository.getWeatherLocation().value
                    tmp?.longitude.toString()+","+tmp?.longitude.toString()
                }else{
                    locationProvider.getSelectedLocationCode()
                })
            }

    }
    fun persistFethedWeatherLocation(weatherLocation: WeatherLocation){
        forecastRepository.persistFetchedWeatherLocation(weatherLocation)
    }
}