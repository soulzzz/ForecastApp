package com.soul.mvvmbase.data.network.repository

import androidx.lifecycle.LiveData
import com.soul.mvvmbase.data.bean.CurrentWeather
import com.soul.mvvmbase.data.bean.WeatherLocation

interface ForecastRepository {
    suspend fun getCurrentWeather(location: String = "101010100"):LiveData<CurrentWeather>
    suspend fun getWeatherLocation():LiveData<WeatherLocation>
     fun persistFetchedWeatherLocation(weatherLocation: WeatherLocation):Unit

     fun isUsingDeviceLocation():Boolean
}