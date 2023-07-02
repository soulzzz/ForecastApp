package com.soul.mvvmbase.data.network

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.soul.mvvmbase.data.network.api.WeatherApiService
import com.soul.mvvmbase.data.network.response.CurrentWeatherResponse
import java.io.IOException

class WeatherNetworkDataSourceImpl(private val weatherApiService: WeatherApiService) : WeatherNetworkDataSource {
    private val _downloadedCurrentWeather = MutableLiveData<CurrentWeatherResponse>()
    override val downloadedCurrentWeather: LiveData<CurrentWeatherResponse>
        get() = _downloadedCurrentWeather

    override suspend fun fetchCurrentWeather(location: String) {
        try {
            val fetchedCurrentWeather = weatherApiService.getCurrentWeather(location)
            _downloadedCurrentWeather.postValue(fetchedCurrentWeather)
        }catch (exception:IOException){

        }
    }
}