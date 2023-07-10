package com.soul.mvvmbase.data.network

import androidx.lifecycle.LiveData
import com.soul.mvvmbase.data.network.response.CurrentWeatherResponse
import com.soul.mvvmbase.data.network.response.FutureWeatherResponse

interface WeatherNetworkDataSource {
    val downloadedCurrentWeather:LiveData<CurrentWeatherResponse>

    val downloadedFutureWeather:LiveData<FutureWeatherResponse>
    suspend fun fetchCurrentWeather(location:String)

    suspend fun fetchFutureWeather(location:String)
}