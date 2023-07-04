package com.soul.mvvmbase.data.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import com.soul.mvvmbase.data.bean.WeatherLocation
import com.soul.mvvmbase.data.network.repository.ForecastRepository
import com.soul.mvvmbase.data.provider.LocationProvider
import kotlinx.coroutines.*

class CurrentWeatherViewModel(
    private val forecastRepository: ForecastRepository,
): ViewModel() {
    private val TAG = javaClass.simpleName
    var weather =  GlobalScope.async(Dispatchers.IO,start = CoroutineStart.LAZY) {
        forecastRepository.getCurrentWeather()
    }
    var weatherLocation = GlobalScope.async(Dispatchers.IO,start = CoroutineStart.LAZY) {
        Log.d(TAG, "weatherLocation: async")
            forecastRepository.getWeatherLocation()
        }

    fun persistFethedWeatherLocation(weatherLocation: WeatherLocation){
        Log.d(TAG, "persistFethedWeatherLocation: ${weatherLocation}")
        forecastRepository.persistFetchedWeatherLocation(weatherLocation)
    }
}