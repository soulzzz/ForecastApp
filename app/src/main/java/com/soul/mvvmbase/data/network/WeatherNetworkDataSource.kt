package com.soul.mvvmbase.data.network

import androidx.lifecycle.LiveData
import com.soul.mvvmbase.data.network.response.CurrentWeatherResponse

interface WeatherNetworkDataSource {
    val downloadedCurrentWeather:LiveData<CurrentWeatherResponse>
    suspend fun fetchCurrentWeather(location:String)
}