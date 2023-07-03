package com.soul.mvvmbase.data.network.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.soul.mvvmbase.data.bean.CurrentWeather
import com.soul.mvvmbase.data.bean.WeatherLocation
import com.soul.mvvmbase.data.db.CurrentWeatherDao
import com.soul.mvvmbase.data.db.WeatherLocationDao
import com.soul.mvvmbase.data.network.WeatherNetworkDataSource
import com.soul.mvvmbase.data.network.response.CurrentWeatherResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.threeten.bp.ZonedDateTime

class ForecastRepositoryImpl(private val currentWeatherDao: CurrentWeatherDao,
                             private val weatherLocationDao: WeatherLocationDao,
                             private val weatherNetworkDataSource: WeatherNetworkDataSource
) : ForecastRepository {
    init {
        weatherNetworkDataSource.downloadedCurrentWeather.observeForever(Observer {
            persistFetchedCurrentWeather(it)
        })
    }
    override suspend fun getCurrentWeather(location: String): LiveData<CurrentWeather> {
       return withContext(Dispatchers.IO){
           initWeatherData(location)
           return@withContext currentWeatherDao.getCurrentWeather()
       }
    }

    override suspend fun getWeatherLocation(): LiveData<WeatherLocation> {
        return withContext(Dispatchers.IO){
            return@withContext weatherLocationDao.getWeatherLocation()
        }
    }

    private fun persistFetchedCurrentWeather(fetchedWeatherResponse: CurrentWeatherResponse){
        GlobalScope.launch(Dispatchers.IO){
            currentWeatherDao.upsert(fetchedWeatherResponse.currentWeather)
        }
    }
    override fun persistFetchedWeatherLocation(weatherLocation: WeatherLocation){
        GlobalScope.launch(Dispatchers.IO){
            weatherLocationDao.upsert(weatherLocation)
        }
    }
    private suspend fun initWeatherData(location: String){
        Log.d("TAG", "initWeatherData: ")
        if(isFetchCurrentNeeded(ZonedDateTime.now().minusHours(1))){
            Log.d("TAG", "initWeatherData2: ")
            weatherNetworkDataSource.fetchCurrentWeather(location)
        }
    }
    private fun isFetchCurrentNeeded(lastFetchedTime:ZonedDateTime):Boolean{
        val thirtyMinutesAgo = ZonedDateTime.now().minusMinutes(30)
        return lastFetchedTime.isBefore(thirtyMinutesAgo)
    }
}