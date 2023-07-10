package com.soul.mvvmbase.data.network

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.soul.mvvmbase.data.network.api.WeatherApiService
import com.soul.mvvmbase.data.network.response.CurrentWeatherResponse
import com.soul.mvvmbase.data.network.response.FutureWeatherResponse
import java.io.IOException

class WeatherNetworkDataSourceImpl(private val weatherApiService: WeatherApiService) : WeatherNetworkDataSource {
    private val _downloadedCurrentWeather = MutableLiveData<CurrentWeatherResponse>()

    private val _downloadedFutureWeather = MutableLiveData<FutureWeatherResponse>()

    override val downloadedCurrentWeather: LiveData<CurrentWeatherResponse>
        get() = _downloadedCurrentWeather
    override val downloadedFutureWeather: LiveData<FutureWeatherResponse>
        get() = _downloadedFutureWeather

    override suspend fun fetchCurrentWeather(location: String) {
        try {
            val fetchedCurrentWeather = weatherApiService.getCurrentWeather(location)
            _downloadedCurrentWeather.postValue(fetchedCurrentWeather)
        }catch (exception:IOException){

        }
    }

    override suspend fun fetchFutureWeather(location: String) {
        try {
            val fetchedFeatureWeather = weatherApiService.getFeatureWeather(location)
            _downloadedFutureWeather.postValue(fetchedFeatureWeather)
        }catch (exception:IOException){

        }
    }
}