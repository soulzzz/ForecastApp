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
import com.soul.mvvmbase.data.provider.LocationProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.threeten.bp.ZonedDateTime
import java.util.*

class ForecastRepositoryImpl(private val currentWeatherDao: CurrentWeatherDao,
                             private val weatherLocationDao: WeatherLocationDao,
                             private val locationProvider: LocationProvider,
                             private val weatherNetworkDataSource: WeatherNetworkDataSource
) : ForecastRepository {
    init {
        weatherNetworkDataSource.downloadedCurrentWeather.observeForever(Observer {
            persistFetchedCurrentWeather(it)
        })
    }
    private val TAG = javaClass.simpleName
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
        if(fetchedWeatherResponse.code == "400") return
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
        val lastWeatherLocation = weatherLocationDao.getWeatherLocation().value
        Log.d(TAG, "initWeatherData from db: ${lastWeatherLocation?.location_time}")
        if(lastWeatherLocation == null || locationProvider.hasLocationChanged(lastWeatherLocation)){
            Log.d(TAG, "initWeatherData db==null or location changed: ")
            fetchCurrentWeather()
            return
        }
        if (isFetchCurrentNeeded(lastWeatherLocation.location_time))
            fetchCurrentWeather()
    }
    private suspend fun fetchCurrentWeather() {
        weatherNetworkDataSource.fetchCurrentWeather(
            locationProvider.getPreferredLocationString()
        )
    }
    private fun isFetchCurrentNeeded(lastFetchedTime:ZonedDateTime):Boolean{

        val thirtyMinutesAgo = ZonedDateTime.now().minusMinutes(30)
        return lastFetchedTime.isBefore(thirtyMinutesAgo)
    }
}