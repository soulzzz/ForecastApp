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
            Log.d(TAG, ":persistFetchedCurrentWeather $it ")
            persistFetchedCurrentWeather(it)
        })
        weatherLocationDao.getWeatherLocation().observeForever {
            Log.d(TAG, ": getWeatherLocation observeForever ${it}" )
            if(it !=null && !it.latitude.equals("null")){
                GlobalScope.launch {
                    reFetchWeatherData()
                }
            }
        }
    }
    private val TAG = javaClass.simpleName
    override suspend fun getCurrentWeather(): LiveData<CurrentWeather> {
       return withContext(Dispatchers.IO){
           initWeatherData()
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
            var tmpWeatherLocation = weatherLocationDao.getLocationNonLive()
            if(tmpWeatherLocation!=null){
                Log.d(TAG, "persistFetchedWeatherLocation: 1 ")
                if(weatherLocation.city!= tmpWeatherLocation.city || isFetchCurrentNeeded(tmpWeatherLocation.location_time)){
                    weatherLocationDao.upsert(weatherLocation)
                    Log.d(TAG, "persistFetchedWeatherLocation:2 ")
                }
            }else{
                Log.d(TAG, "persistFetchedWeatherLocation: 3 ")
                weatherLocationDao.upsert(weatherLocation)
            }



        }
    }

    override fun isUsingDeviceLocation(): Boolean = locationProvider.isUsingDeviceLocation()


    private suspend fun initWeatherData(){
        if(locationProvider.isUsingDeviceLocation()){
            Log.d(TAG, "initWeatherData: isUsingDeviceLocation")
            val lastWeatherLocation = weatherLocationDao.getLocationNonLive()
            Log.d(TAG, "initWeatherData from db: ${lastWeatherLocation?.location_time}")
            if(lastWeatherLocation == null || locationProvider.hasLocationChanged(lastWeatherLocation)){
                Log.d(TAG, "initWeatherData db==null or location changed: ")
                fetchCurrentWeather()
                return
            }
            if (isFetchCurrentNeeded(lastWeatherLocation.location_time)){
                fetchCurrentWeather()
            }
        }else{
            Log.d(TAG, "initWeatherData: NotUsingDeviceLocation ")
            fetchCurrentWeather()
            persistFetchedWeatherLocation(WeatherLocation(city = locationProvider.getSelectedLocationName()))
        }

    }
    private suspend fun fetchCurrentWeather() {
        weatherNetworkDataSource.fetchCurrentWeather(
            locationProvider.getPreferredLocationString()
        )
    }
    private suspend fun reFetchWeatherData(){
        val lastWeatherLocation = weatherLocationDao.getLocationNonLive()
        Log.d(TAG, "reFetchWeatherData from db: ${lastWeatherLocation?.location_time}")
        fetchCurrentWeather()
    }
    private fun isFetchCurrentNeeded(lastFetchedTime:ZonedDateTime):Boolean{

        val thirtyMinutesAgo = ZonedDateTime.now().minusMinutes(30)
        return lastFetchedTime.isBefore(thirtyMinutesAgo)
    }
}