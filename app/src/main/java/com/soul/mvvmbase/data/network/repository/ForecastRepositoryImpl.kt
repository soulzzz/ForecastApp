package com.soul.mvvmbase.data.network.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.soul.mvvmbase.data.bean.CurrentWeather
import com.soul.mvvmbase.data.db.CurrentWeatherDao
import com.soul.mvvmbase.data.network.WeatherNetworkDataSource
import com.soul.mvvmbase.data.network.response.CurrentWeatherResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.threeten.bp.ZonedDateTime

class ForecastRepositoryImpl(private val currentWeatherDao: CurrentWeatherDao,
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
    private fun persistFetchedCurrentWeather(fetchedWeatherResponse: CurrentWeatherResponse){
        GlobalScope.launch(Dispatchers.IO){
            currentWeatherDao.upsert(fetchedWeatherResponse.currentWeather)
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