package com.soul.mvvmbase.data.network.repository

import androidx.lifecycle.LiveData
import com.soul.mvvmbase.data.bean.CurrentWeather
import com.soul.mvvmbase.data.bean.DailyWeather
import com.soul.mvvmbase.data.bean.WeatherLocation
import org.threeten.bp.LocalDate

interface ForecastRepository {
//    suspend fun getFutureWeather():LiveData<CurrentWeather>
    suspend fun getCurrentWeather():LiveData<CurrentWeather>
    suspend fun getWeatherLocation():LiveData<WeatherLocation>
     fun persistFetchedWeatherLocation(weatherLocation: WeatherLocation):Unit

     fun isUsingDeviceLocation():Boolean

    suspend fun getFutureWeatherList(startDate: LocalDate): LiveData<out List<DailyWeather>>

    suspend fun getFutureWeatherByDate(date: LocalDate): LiveData<out DailyWeather>

}